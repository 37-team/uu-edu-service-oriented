package com.bigitcompany.cloudaireadmodel.common.domain.services;

import java.util.ArrayList;
import java.util.List;

public class ChunkMaker<T> {

    private final int chunkSize;

    public ChunkMaker(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public List<List<T>> breakIntoChunks(List<T> list) {

        List<List<T>> chunks = new ArrayList<>();

        for (int i = 0; i < list.size(); i += chunkSize) {
            chunks.add(list.subList(i, Math.min(i + chunkSize, list.size())));
        }
        return chunks;
    }

}
