package com.example.apitest;

import java.util.List;

// TfcacdarM 배열의 원소는 head 블록({"head":[...]}) 아니면 row 블록({"row":[...]}) — 없는 쪽은 null
public class BlockModel {
    public List<HeadModel> head;
    public List<ItemModel> row;
}
