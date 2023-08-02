package lovely.jia;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class JiaApplication{


    public static void main(String[] args) {
        System.out.println("프로그램 구동중 입니다...");
        SpringApplication.run(JiaApplication.class, args);
        System.out.println("프로그램이 종료 되었습니다...");
    }

}
