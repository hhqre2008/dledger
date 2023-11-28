/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.openmessaging.storage.dledger.protocol;

import io.openmessaging.storage.dledger.entry.DLedgerEntry;
import io.openmessaging.storage.dledger.utils.PreConditions;
import java.util.ArrayList;
import java.util.List;

public class PushEntryRequest extends RequestOrResponse {
    private long commitIndex = -1;
    private Type type = Type.APPEND;
    private DLedgerEntry entry;

    //for batch append push
    private List<DLedgerEntry> batchEntry = new ArrayList<>();
    private int totalSize;

    public DLedgerEntry getEntry() {
        return entry;
    }

    public void setEntry(DLedgerEntry entry) {
        this.entry = entry;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public long getCommitIndex() {
        return commitIndex;
    }

    public void setCommitIndex(long commitIndex) {
        this.commitIndex = commitIndex;
    }

    public void addEntry(DLedgerEntry entry) {
        if (!batchEntry.isEmpty()) {
            PreConditions.check(batchEntry.get(0).getIndex() + batchEntry.size() == entry.getIndex(),
                DLedgerResponseCode.UNKNOWN, "batch push wrong order");
        }
        batchEntry.add(entry);
        totalSize += entry.getSize();
    }

    public long getFirstEntryIndex() {
        if (!batchEntry.isEmpty()) {
            return batchEntry.get(0).getIndex();
        } else if (entry != null) {
            return entry.getIndex();
        } else {
            return -1;
        }
    }

    public long getLastEntryIndex() {
        if (!batchEntry.isEmpty()) {
            return batchEntry.get(batchEntry.size() - 1).getIndex();
        } else if (entry != null) {
            return entry.getIndex();
        } else {
            return -1;
        }
    }

    public int getCount() {
        return batchEntry.size();
    }

    public long getTotalSize() {
        return totalSize;
    }

    public List<DLedgerEntry> getBatchEntry() {
        return batchEntry;
    }

    public void clear() {
        batchEntry.clear();
        totalSize = 0;
    }

    public boolean isBatch() {
        return !batchEntry.isEmpty();
    }

    public enum Type {
        /**
         * 日志条目追加到从节点
         */
        APPEND,
        /**
         * 通常，Leader会将提交的索引附加到append请求，但是如果append请求很少且分散，leader将发送要给单独的请求来通知从节点提交的索引
         */
        COMMIT,
        /**
         * 如果Leader发生变化，新的Leader需要与它的从节点的日志条目进行比较，以便截断从节点多余的数据
         */
        COMPARE,
        /**
         * 如果Leader通过索引完成日志对比，则Leader将发送TRUNCATE给它的从节点
         */
        TRUNCATE
    }
}
