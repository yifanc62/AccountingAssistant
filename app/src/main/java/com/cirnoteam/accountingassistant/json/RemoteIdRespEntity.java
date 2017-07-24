package com.cirnoteam.accountingassistant.json;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Yifan on 2017/7/23.
 */
public class RemoteIdRespEntity {
    private Map<Long, Long> idMap;

    public RemoteIdRespEntity() {
        this.idMap = new LinkedHashMap<>();
    }

    public Map<Long, Long> getIdMap() {
        return idMap;
    }

    public RemoteIdRespEntity setIdMap(Map<Long, Long> idMap) {
        this.idMap = idMap;
        return this;
    }

    public RemoteIdRespEntity addIdPair(Long id, Long remoteId) {
        idMap.put(id, remoteId);
        return this;
    }
}
