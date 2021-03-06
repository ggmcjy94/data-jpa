package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
public class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired MemberQueryRepository memberQueryRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() throws Exception {
        //given
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        //when
        Member findMember = memberRepository.findById(savedMember.getId()).get();
        //then
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember).isEqualTo(member);

    }

    @Test
    public void basicCRUD() throws Exception {

        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운터
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAheGreaterThen() throws Exception {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }
    
    @Test
    public void testNamedQuery() throws Exception {
        Member member1 = new Member("BBB", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);
        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(member2);
    }
    @Test
    public void testQuery() throws Exception {
        Member member1 = new Member("BBB", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);
        List<Member> result = memberRepository.findUser("AAA", 20);
        assertThat(result.get(0)).isEqualTo(member2);
    }

    @Test
    public void findUserNameList() throws Exception {
        Member member1 = new Member("BBB", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> usernameList = memberRepository.findUserNameList();
        for (String s : usernameList) {
            System.out.println("s => " + s);
        }
        assertThat(usernameList.get(0)).isEqualTo("BBB");
    }

    @Test
    public void findMemberDto() throws Exception {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("AAA", 10);
        member1.setTeam(team);
        memberRepository.save(member1);


        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto => " + dto);
        }
    }

    @Test
    public void findByNames() throws Exception {
        Member member1 = new Member("BBB", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        for (Member member : result) {
            System.out.println("member => " + member);

        }
    }
    @Test
    public void returnType() throws Exception {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> aaa = memberRepository.findListByUsername("AAA");
        Member aaa1 = memberRepository.findMemberByUsername("asdasd");
        Optional<Member> aaa2 = memberRepository.findOptionalByUsername("asdasd");
        System.out.println("aaa : " + aaa);
        System.out.println("aaa1 : " + aaa1);
        System.out.println("aaa2 : " + aaa2);
    }

    @Test
    public void paging() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));


        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));


        Page<Member> page = memberRepository.findByAge(age ,pageRequest);

        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null)); //dto 변환

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }


    @Test
    public void  bulkUpdate() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        int resultCount = memberRepository.bulkAgePlus(20); //41 나옴
//        em.flush(); //벌크 연산후  영속성 컨텍스트를 날림
//        em.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 => " + member5); // 벌크 연산에서 조심할점 40 나옴

        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1" , 10 ,teamA);
        Member member2 = new Member("member2" , 10 ,teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // 1 + 2
        //List<Member> members = memberRepository.findAll();
        //List<Member> members = memberRepository.findMemberFetchJoin();
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : members) {
            System.out.println("memberUsername => " + member.getUsername());
            System.out.println("memberTeamClass => " + member.getTeam().getClass());
            //이때 쿼리를 날려서 team name 을 가져옴 n + 1
            System.out.println("memberTeamName -> " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //Member findMember = memberRepository.findById(member1.getId()).get();
        Member findMember = memberRepository.findReadOnlyByUsername("member1");

        findMember.setUsername("member2"); // 변경 감지 동작

        em.flush(); //상태를 바꼈다고 인지 해줌


    }

    @Test
    public void Lock() {
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        List<Member> result = memberRepository.findLockByUsername("member1");

    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom(); // 인터페이스 명으로 해결되지않을때 쓴다 복잡한 쿼리 문을 사용할때 사용 쪼갤떄 사용
    }

    @Test
    public void specBasic() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA")); //실무에서 안씀 query dsl 을 사용
        List<Member> result = memberRepository.findAll(spec);


        Assertions.assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void queryByExample(){
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();// db에 insert 날리고
        em.clear(); //영속성컨택스트 캐시 클리어

        //when
        //Probe
        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher); //실무에서 잘안씀 JOIN 에서 해결이 잘안된다. 내부조인 가능 하지만 외부 조인 불가능

        List<Member> result = memberRepository.findAll(example);

        assertThat(result.get(0).getUsername()).isEqualTo("m1");
    }


    @Test
    public void projections() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();// db에 insert 날리고
        em.clear(); //영속성컨택스트 캐시 클리어

        //when
        //List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1",UsernameOnlyDto.class);
        List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("m1",NestedClosedProjections.class);

        for (NestedClosedProjections nestedClosedProjections : result) {
            String username = nestedClosedProjections.getUsername();
            System.out.println("username => " + username);
            String teamName = nestedClosedProjections.getTeam().getName();
            System.out.println("teamName => " + teamName);
        }

    }

    @Test
    public void nativeQuery() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();// db에 insert 날리고
        em.clear(); //영속성컨택스트 캐시 클리어

        //Member result = memberRepository.findByNativeQuery("m1");
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        for (MemberProjection memberProjection : result) {
            System.out.println("memberProjection => " + memberProjection.getUsername());
            System.out.println("memberProjection => " + memberProjection.getTeamName());
        }

    }
}
