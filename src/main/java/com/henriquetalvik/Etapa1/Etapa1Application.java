package com.henriquetalvik.Etapa1;

import com.henriquetalvik.principal.Principal;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Etapa1Application {

	public static void main(String[] args) {
		SpringApplication.run(Etapa1Application.class, args);
                
                Principal principal = new Principal();
                principal.exibeMenu();
	}

}
