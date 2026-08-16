package com.example.apitest;

import java.util.List;

// 정상 응답: {"TfcacdarM":[{"head":[...]},{"row":[...]}]}
// 에러·무데이터 응답(HTTP 200): 래퍼 없이 최상위가 바로 {"RESULT":{...}} 로 온다
public class PageListModel {
    public List<BlockModel> TfcacdarM;
    public ResultModel RESULT;

    public List<ItemModel> findRows() {
        if (TfcacdarM == null) return null;
        for (BlockModel block : TfcacdarM) {
            if (block != null && block.row != null) return block.row;
        }
        return null;
    }

    public ResultModel findResult() {
        if (RESULT != null) return RESULT;
        if (TfcacdarM == null) return null;
        for (BlockModel block : TfcacdarM) {
            if (block == null || block.head == null) continue;
            for (HeadModel head : block.head) {
                if (head != null && head.RESULT != null) return head.RESULT;
            }
        }
        return null;
    }

    public int totalCount() {
        if (TfcacdarM == null) return 0;
        for (BlockModel block : TfcacdarM) {
            if (block == null || block.head == null) continue;
            for (HeadModel head : block.head) {
                if (head != null && head.list_total_count != null) return head.list_total_count;
            }
        }
        return 0;
    }
}
