# 사고다발지 현황 OpenAPI 스펙

`tools/fetch_api_spec.py` 가 생성한 문서. 수정하지 말고 재생성할 것.

- data.go.kr 문서: https://www.data.go.kr/data/15057393/openapi.do (LINK 타입)
- 링크 대상: https://data.gg.go.kr/portal/data/service/selectServicePage.do?page=1&sortColumn=&sortDirection=&infId=9HJ306A05WF6RS2560PG21056899&infSeq=3
- infId: `9HJ306A05WF6RS2560PG21056899` / infSeq: 3
- **요청주소: `https://openapi.gg.go.kr/TfcacdarM`** (https 전용 — http 는 503)
- 트래픽 제한: 없음

## 공통 요청 인자 (포털 전역, 대소문자 엄격)

| 이름 | 필수 | 타입 | 설명 |
|---|---|---|---|
| KEY | 필수 | STRING | 인증키 |
| Type | 선택 | STRING | xml(기본) 또는 json |
| pIndex | 선택 | INTEGER | 페이지 위치 (기본 1) |
| pSize | 선택 | INTEGER | 페이지당 건수 (기본 100, 최대 1000) |

## 서비스 고유 요청 인자 (variables)

| colId | reqType | reqNeed | colNm | colExp |
|---|---|---|---|---|
| SIGUN_NM | STRING | N | 시군명 | 시군명 |
| SIGUN_CD | STRING | N | 시군코드 | 시군코드 |

## 출력 컬럼 (columns)

| colId | colNm | unitNm | colExp |
|---|---|---|---|
| LIST_TOTAL_COUNT | 행총건수 | None | 행총건수 |
| CODE | 응답결과코드 | None | 응답결과코드 |
| MESSAGE | 응답결과메세지 | None | 응답결과메세지 |
| API_VERSION | API버전 | None | API버전 |
| SIGUN_NM | 시군명 | None | 시군명 |
| ACDNT_YY | 사고년도 | None | 사고년도 |
| ACDNT_DIV_NM | 사고유형구분 | None | 사고유형구분 |
| MULTI_KNOWLG_DIV_NO | 다발지식별자 | None | 다발지식별자 |
| MULTI_KNOWLG_DIV_GROUP_NO | 다발지역그룹식별자 | None | 다발지역그룹식별자 |
| LEGALDONG_CD_NO | 법정동코드 | None | 법정동코드 |
| SPOT_NO | 위치코드 | None | 위치코드 |
| JURISD_POLCSTTN_NM | 시도시군구명 | None | 시도시군구명 |
| LOC_INFO | 사고지역위치명 | None | 사고지역위치명 |
| OCCUR_CNT | 발생건수 | None | 발생건수 |
| CASLT_CNT | 사상자수 | None | 사상자수 |
| DPRS_CNT | 사망자수 | None | 사망자수 |
| SERINJRY_INDVDL_CNT | 중상자수 | None | 중상자수 |
| SLTINJRY_INDVDL_CNT | 경상자수 | None | 경상자수 |
| INJURY_APLCNT_CNT | 부상자수 | None | 부상자수 |
| LAT | 위도 | None | 위도 |
| LOGT | 경도 | None | 경도 |
| MULTI_REGION_INFO | 사고다발지역폴리곤정보 | None | 사고다발지역폴리곤정보 |
| SIGUN_CD | 시군코드 | None | 시군코드 |

## 응답 메시지 코드 (messages)

| msgTag | msgCd | msgExp |
|---|---|---|
| ERROR | 300 | 필수 값이 누락되어 있습니다. 요청인자를 참고 하십시오. |
| ERROR | 290 | 인증키가 유효하지 않습니다. 인증키가 없는 경우, 홈페이지에서 인증키를 신청하십시오. |
| ERROR | 310 | 해당하는 서비스를 찾을 수 없습니다. 요청인자 중 SERVICE를 확인하십시오. |
| ERROR | 333 | 요청위치 값의 타입이 유효하지 않습니다.요청위치 값은 정수를 입력하세요. |
| ERROR | 336 | 데이터요청은 한번에 최대 1,000건을 넘을 수 없습니다. |
| ERROR | 337 | 일별 트래픽 제한을 넘은 호출입니다. 오늘은 더이상 호출할 수 없습니다. |
| ERROR | 500 | 서버 오류입니다. 지속적으로 발생시 홈페이지로 문의(Q&amp;A) 바랍니다. |
| ERROR | 600 | 데이터베이스 연결 오류입니다. 지속적으로 발생시 홈페이지로 문의(Q&amp;A) 바랍니다. |
| ERROR | 601 | SQL 문장 오류 입니다. 지속적으로 발생시 홈페이지로 문의(Q&amp;A) 바랍니다. |
| INFO | 000 | 정상 처리되었습니다. |
| INFO | 300 | 관리자에 의해 인증키 사용이 제한되었습니다. |
| INFO | 200 | 해당하는 데이터가 없습니다. |

## 테스트 호출

`GET https://openapi.gg.go.kr/TfcacdarM?KEY=<발급키>&Type=json&pIndex=1&pSize=5` → HTTP 200

- RESULT: INFO-000 정상 처리되었습니다.
- list_total_count: 2611
- 수신 row: 5건

정상 봉투는 `{"TfcacdarM": [{"head": [...]}, {"row": [...]}]}` 형태이고,
에러·무데이터 시에는 래퍼 없이 최상위가 `{"RESULT": {...}}` 로 무너진다 (HTTP 는 둘 다 200).

첫 번째 row 원본:

```json
{
  "SIGUN_NM": "오산시",
  "SIGUN_CD": "41370",
  "ACDNT_DIV_NM": "보행어린이사고다발지",
  "MULTI_KNOWLG_DIV_NO": 6807696,
  "MULTI_KNOWLG_DIV_GROUP_NO": 2022084,
  "LEGALDONG_CD_NO": "4137010300",
  "SPOT_NO": "41370001",
  "JURISD_POLCSTTN_NM": "경기도 오산시1",
  "LOC_INFO": "경기도 오산시 원동(씨티월3입구 부근)",
  "OCCUR_CNT": 3,
  "CASLT_CNT": 3,
  "DPRS_CNT": 0,
  "SERINJRY_INDVDL_CNT": 0,
  "SLTINJRY_INDVDL_CNT": 3,
  "INJURY_APLCNT_CNT": 0,
  "LOGT": 127.0745293027,
  "LAT": 37.146432443,
  "MULTI_REGION_INFO": "{'type':'Polygon','coordinates':[[[127.07632593,37.14643244],[127.07629141,37.14615306],[127.07618917,37.14588441],[127.07602315,37.14563682],[127.07579971,37.1454198],[127.07552746,37.1452417],[127.07521684,37.14510936],[127.07487981,37.14502786],[127.0745293,37.14500034],[127.0741788,37.14502786],[127.07384176,37.14510936],[127.07353115,37.1452417],[127.07325889,37.1454198],[127.07303546,37.14563682],[127.07286943,37.14588441],[127.07276719,37.14615306],[127.07273267,37.14643244],[127.07276719,37.14671183],[127.07286943,37.14698048],[127.07303546,37.14722806],[127.07325889,37.14744507],[127.07353115,37.14762317],[127.07384176,37.14775551],[127.0741788,37.147837],[127.0745293,37.14786451],[127.07487981,37.147837],[127.07521684,37.14775551],[127.07552746,37.14762317],[127.07579971,37.14744507],[127.07602315,37.14722806],[127.07618917,37.14698048],[127.07629141,37.14671183],[127.07632593,37.14643244]]]}",
  "ACDNT_YY": "2021"
}
```
