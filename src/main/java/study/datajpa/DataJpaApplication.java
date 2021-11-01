package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;



@EnableJpaAuditing // 등록일 수정일 땜에 이걸 넣었음 나중에 서치 후 구조 파악
@SpringBootApplication
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	//생성자 수정자
	@Bean //
	public AuditorAware<String> auditorProvider () {
		return new AuditorAware<String>() {
			@Override
			public Optional<String> getCurrentAuditor() {
				return Optional.of(UUID.randomUUID().toString()); // 여기다 해당 유저 아이디 넣으면 됌 이것도 서치후 구조파악 세션에서 꺼내서 넣으면 된당.
			}
		};
	}
}
