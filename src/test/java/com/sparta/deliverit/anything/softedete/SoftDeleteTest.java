package com.sparta.deliverit.anything.softedete;

import com.sparta.deliverit.anything.advice.SoftDeleteFilterTxAdvice;
import com.sparta.deliverit.anything.config.AuditingConfig;
import com.sparta.deliverit.anything.softedete.example.TestEntity;
import com.sparta.deliverit.anything.softedete.example.TestEntityRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@EntityScan(basePackageClasses = { TestEntity.class })
@Import({AuditingConfig.class, SoftDeleteTest.TestConfig.class, SoftDeleteTest.TestBoot.class, SoftDeleteFilterTxAdvice.class})
public class SoftDeleteTest {

    @Autowired
    private TestEntityRepository repository;

    @Autowired
    private EntityManager em;

    @Autowired
    private TxService txSvc;

    @Test
    @DisplayName("소프트 삭제하면 조회되지 않는다")
    void saveDeleteAndCountAlive() {
        int size = txSvc.saveDeleteAndCountAlive();
        assertEquals(1, size);
    }

    @Test
    @DisplayName("소프트 삭제 필터를 비활성화하면 삭제된 엔티티도 조회된다")
    void saveDeleteAndCountAllWithFilterOff() {
        int size = txSvc.saveDeleteAndCountAllWithFilterOff();
        assertEquals(2, size);
    }

    @Configuration
    @EnableAspectJAutoProxy // AOP 활성화
    static class TestConfig {
        @Bean TxService txSvc(TestEntityRepository repo, EntityManager em) { return new TxService(repo, em); }
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EnableAspectJAutoProxy
    static class TestBoot { }

    static class TxService {
        private final TestEntityRepository repo;
        private final EntityManager em;
        TxService(TestEntityRepository repo, EntityManager em) { this.repo = repo; this.em = em; }

        @Transactional
        public int saveDeleteAndCountAlive() {
            repo.saveAll(List.of(new TestEntity(), new TestEntity()));
            em.flush(); em.clear();

            Long id = repo.findAll().get(0).getId();
            repo.deleteById(id); // @SQLDelete → UPDATE deleted_at
            em.flush(); em.clear();

            return repo.findAll().size(); // 필터 ON → 1
        }

        @Transactional
        public int saveDeleteAndCountAllWithFilterOff() {
            repo.saveAll(List.of(new TestEntity(), new TestEntity()));
            em.flush(); em.clear();

            Long id = repo.findAll().get(0).getId();
            repo.deleteById(id);
            em.flush(); em.clear();

            Session s = em.unwrap(Session.class);
            boolean was = s.getEnabledFilter("softDeleteFilter") != null;
            if (was) s.disableFilter("softDeleteFilter");
            try { return repo.findAll().size(); } // 필터 OFF → 2
            finally { if (was) s.enableFilter("softDeleteFilter"); }
        }
    }
}
