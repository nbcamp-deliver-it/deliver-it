package com.sparta.deliverit.anything.advice;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.Ordered;
import org.hibernate.Session;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class SoftDeleteFilterTxAdvice {

    @PersistenceContext
    private EntityManager entityManager;

    @Before("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void enableOnMethodTx() {
        enableFilter();
    }

    @Before("@within(org.springframework.transaction.annotation.Transactional)")
    public void enableOnClassTx() {
        enableFilter();
    }

    private void enableFilter() {
        Session session = entityManager.unwrap(Session.class);
        if (session.getEnabledFilter("softDeleteFilter") == null) {
            session.enableFilter("softDeleteFilter");
        }
    }
}
