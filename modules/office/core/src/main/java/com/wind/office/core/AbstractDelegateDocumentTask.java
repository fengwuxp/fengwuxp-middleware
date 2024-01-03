package com.wind.office.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * @author wuxp
 * @date 2023-10-27 19:55
 **/
@Getter
@Slf4j
public abstract class AbstractDelegateDocumentTask implements OfficeDocumentTask {

    private final OfficeDocumentTaskInfo delegate;

    protected AbstractDelegateDocumentTask(OfficeDocumentTaskInfo delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public LocalDateTime getBeginTime() {
        return delegate.getBeginTime();
    }

    @Override
    public LocalDateTime getEndTime() {
        return delegate.getEndTime();
    }

    @Override
    public OfficeTaskState getState() {
        return delegate.getState();
    }


    @Override
    public void updateState(OfficeTaskState newState) {
        delegate.updateState(newState);
    }

    @Override
    public void addRow(Object row) {
        delegate.addRow(row);
    }

    @Override
    public void addFailedRow(Object row) {
        delegate.addFailedRow(row);
    }

    @Override
    public int getRowSize() {
        return delegate.getRowSize();
    }

    @Override
    public int getFailedRowSize() {
        return delegate.getFailedRowSize();
    }

    protected abstract void doTask();

    @Override
    public void run() {
        try {
            updateState(OfficeTaskState.EXECUTING);
            doTask();
            updateState(OfficeTaskState.COMPLETED);
        } catch (Throwable throwable) {
            log.error("office document task exec error", throwable);
            updateState(OfficeTaskState.FAILED);
        }
    }
}
