package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

//projections
public interface UsernameOnly {


//    @Value("#{target.username + ' ' + target.age}") //open
    String getUsername(); // close
}
