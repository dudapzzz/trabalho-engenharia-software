package br.ufsm.poli.csi.tapw.prova1;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "API prova 1",
                version = "1.0",
                description = "API para controle de embarcações registradas no estado",
                contact = @Contact(name = "Ana Julia Bock Medina", email = "ana.bock@acad.ufsm.br")
        )
)

@SpringBootApplication
public class Prova1Application {

    public static void main(String[] args) {
        SpringApplication.run(Prova1Application.class, args);
    }

}
