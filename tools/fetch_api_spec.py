"""data.go.kr 15057393 문서에서 실제 OpenAPI 스펙을 추출하고 테스트 호출한다.

사용법:
    source .venv/bin/activate
    python tools/fetch_api_spec.py > docs/API_SPEC.md

15057393 은 LINK 타입이라 data.go.kr 에는 스펙이 없다. 스펙의 원천은 경기데이터드림의
selectOpenApiMeta.do (JSON) 이다 — 서비스 페이지 HTML 은 클라이언트 렌더링 전의
placeholder(TBKINTEXHOTELLISTM)만 담고 있으므로 파싱 대상이 아니다.
GET 만 사용한다: data.go.kr 의 addApiLinkPrcuse.do (POST) 는 활용신청 레코드를
INSERT 하는 부작용이 있어 호출하면 안 된다.
"""

import json
import sys
from urllib.parse import parse_qs, urlparse

import requests

PUBLIC_DATA_PK = "15057393"
API_KEY = "12808f33bce443cbb6e8742300db323c"
UA = {"User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36"}


def md_table(rows: list[dict]) -> str:
    """dict 목록을 키 합집합 컬럼의 마크다운 표로 만든다 (스키마 변동에 안전)."""
    if not rows:
        return "(없음)\n"
    cols: list[str] = []
    for row in rows:
        for key in row:
            if key not in cols:
                cols.append(key)
    lines = [
        "| " + " | ".join(cols) + " |",
        "|" + "---|" * len(cols),
    ]
    for row in rows:
        lines.append("| " + " | ".join(str(row.get(c, "")).replace("\n", " ") for c in cols) + " |")
    return "\n".join(lines) + "\n"


def main() -> None:
    # 1. data.go.kr LINK 리다이렉트 조회 → 경기데이터드림 infId 추출
    link = requests.get(
        "https://www.data.go.kr/tcs/dss/selectApiLinkUrl.do",
        params={"publicDataPk": PUBLIC_DATA_PK},
        headers=UA,
        timeout=30,
    ).json()
    link_url = link["linkUrl"]
    query = parse_qs(urlparse(link_url).query)
    inf_id = query["infId"][0]
    inf_seq = query.get("infSeq", ["3"])[0]

    # 2. 경기데이터드림 OpenAPI 메타 (스펙 원천)
    meta = requests.get(
        "https://data.gg.go.kr/portal/data/openapi/selectOpenApiMeta.do",
        params={"infId": inf_id, "infSeq": inf_seq},
        headers=UA,
        timeout=30,
    ).json()
    endpoint = f"{meta['apiEp']}/{meta['apiRes']}"

    print(f"# {meta.get('infNm', '(이름 없음)')} OpenAPI 스펙")
    print()
    print("`tools/fetch_api_spec.py` 가 생성한 문서. 수정하지 말고 재생성할 것.")
    print()
    print(f"- data.go.kr 문서: https://www.data.go.kr/data/{PUBLIC_DATA_PK}/openapi.do (LINK 타입)")
    print(f"- 링크 대상: {link_url}")
    print(f"- infId: `{inf_id}` / infSeq: {inf_seq}")
    print(f"- **요청주소: `{endpoint}`** (https 전용 — http 는 503)")
    print(f"- 트래픽 제한: {'없음' if str(meta.get('apiTrf')) == '0' else meta.get('apiTrf')}")
    print()
    print("## 공통 요청 인자 (포털 전역, 대소문자 엄격)")
    print()
    print(md_table([
        {"이름": "KEY", "필수": "필수", "타입": "STRING", "설명": "인증키"},
        {"이름": "Type", "필수": "선택", "타입": "STRING", "설명": "xml(기본) 또는 json"},
        {"이름": "pIndex", "필수": "선택", "타입": "INTEGER", "설명": "페이지 위치 (기본 1)"},
        {"이름": "pSize", "필수": "선택", "타입": "INTEGER", "설명": "페이지당 건수 (기본 100, 최대 1000)"},
    ]))
    print("## 서비스 고유 요청 인자 (variables)")
    print()
    print(md_table(meta.get("variables") or []))
    print("## 출력 컬럼 (columns)")
    print()
    print(md_table(meta.get("columns") or []))
    print("## 응답 메시지 코드 (messages)")
    print()
    print(md_table(meta.get("messages") or []))

    # 3. 실 호출로 JSON 봉투 확인 (문서에는 키를 노출하지 않는다)
    resp = requests.get(
        endpoint,
        params={"KEY": API_KEY, "Type": "json", "pIndex": 1, "pSize": 5},
        headers=UA,
        timeout=30,
    )
    # Content-Type 이 text/html 로 오지만 본문은 JSON — 직접 파싱한다
    data = json.loads(resp.text)

    print("## 테스트 호출")
    print()
    print(f"`GET {endpoint}?KEY=<발급키>&Type=json&pIndex=1&pSize=5` → HTTP {resp.status_code}")
    print()
    service = data.get(meta["apiRes"])
    if service is None:
        # 에러·무데이터면 래퍼 없이 최상위가 바로 {"RESULT":{...}}
        print("응답이 RESULT 봉투로 왔습니다 (에러 또는 데이터 없음):")
        print()
        print("```json")
        print(json.dumps(data, ensure_ascii=False, indent=2))
        print("```")
        sys.exit(1)

    head = {}
    rows = []
    for block in service:
        for entry in block.get("head", []):
            head.update(entry)
        rows.extend(block.get("row", []))
    result = head.get("RESULT", {})
    print(f"- RESULT: {result.get('CODE')} {result.get('MESSAGE')}")
    print(f"- list_total_count: {head.get('list_total_count')}")
    print(f"- 수신 row: {len(rows)}건")
    print()
    print("정상 봉투는 `{\"" + meta["apiRes"] + "\": [{\"head\": [...]}, {\"row\": [...]}]}` 형태이고,")
    print("에러·무데이터 시에는 래퍼 없이 최상위가 `{\"RESULT\": {...}}` 로 무너진다 (HTTP 는 둘 다 200).")
    print()
    if rows:
        print("첫 번째 row 원본:")
        print()
        print("```json")
        print(json.dumps(rows[0], ensure_ascii=False, indent=2))
        print("```")


if __name__ == "__main__":
    main()
