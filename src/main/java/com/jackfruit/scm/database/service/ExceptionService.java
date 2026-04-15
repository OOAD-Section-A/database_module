package com.jackfruit.scm.database.service;

import com.jackfruit.scm.database.dao.ExceptionLogDao;
import com.jackfruit.scm.database.model.SubsystemException;
import com.jackfruit.scm.database.util.ValidationUtils;
import java.util.List;
import java.util.Optional;

public class ExceptionService {

    private final ExceptionLogDao exceptionLogDao;

    public ExceptionService(ExceptionLogDao exceptionLogDao) {
        this.exceptionLogDao = exceptionLogDao;
    }

    public void logException(SubsystemException subsystemException) {
        validate(subsystemException);
        exceptionLogDao.save(subsystemException);
    }

    public void updateException(SubsystemException subsystemException) {
        validate(subsystemException);
        exceptionLogDao.update(subsystemException);
    }

    public Optional<SubsystemException> getException(String exceptionId) {
        ValidationUtils.requireText(exceptionId, "exceptionId");
        return exceptionLogDao.findById(exceptionId);
    }

    public List<SubsystemException> getExceptions() {
        return exceptionLogDao.findAll();
    }

    private void validate(SubsystemException subsystemException) {
        ValidationUtils.requireText(subsystemException.getExceptionId(), "exceptionId");
        ValidationUtils.requireText(subsystemException.getSubsystemName(), "subsystemName");
        ValidationUtils.requireText(subsystemException.getSeverity(), "severity");
        ValidationUtils.requireText(subsystemException.getExceptionMessage(), "exceptionMessage");
        ValidationUtils.requireText(subsystemException.getStatus(), "status");
        if (subsystemException.getTimestampUtc() == null) {
            throw new IllegalArgumentException("timestampUtc cannot be null");
        }
    }
}
