package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final EntityManager em;
    List<Member> findAllMembers() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList(); // 예를 들어 설명 복접한 쿼리 로직을 쓸때 사용 커스텀 과 같은 방식

    }
}
