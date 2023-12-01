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

package io.openmessaging.storage.dledger.store;

import io.openmessaging.storage.dledger.MemberState;
import io.openmessaging.storage.dledger.entry.DLedgerEntry;

/**
 * 存储抽象类
 */
public abstract class DLedgerStore {

    public MemberState getMemberState() {
        return null;
    }
    //向主节点追加日志
    public abstract DLedgerEntry appendAsLeader(DLedgerEntry entry);
    //向从节点同步日志
    public abstract DLedgerEntry appendAsFollower(DLedgerEntry entry, long leaderTerm, String leaderId);
    //根据日志下标查找日志
    public abstract DLedgerEntry get(Long index);
    //获取已提交的下标
    public abstract long getCommittedIndex();
    //更新commitedIndex的值，为空实现，由具体的存储子类实现
    public void updateCommittedIndex(long term, long committedIndex) {

    }
    //获取Leader当前最大的投票轮次
    public abstract long getLedgerEndTerm();
    //获取Leader下一条日志写入的下标
    public abstract long getLedgerEndIndex();
    //获取Leader第一条消息的下标
    public abstract long getLedgerBeginIndex();
    //更新Leader维护的ledgerEndIndex和ledgerEndTerm
    protected void updateLedgerEndIndexAndTerm() {
        if (getMemberState() != null) {
            getMemberState().updateLedgerIndexAndTerm(getLedgerEndIndex(), getLedgerEndTerm());
        }
    }
    //刷写，空方法，由具体子类实现
    public void flush() {

    }
    //删除日志，空方法，由具体子类实现
    public long truncate(DLedgerEntry entry, long leaderTerm, String leaderId) {
        return -1;
    }
    //启动存储管理器，空方法，由具体子类实现
    public void startup() {

    }
    //关闭存储管理器，空方法，由具体子类实现
    public void shutdown() {

    }
}
