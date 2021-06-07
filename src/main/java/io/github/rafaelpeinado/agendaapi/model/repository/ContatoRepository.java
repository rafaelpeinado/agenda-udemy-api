package io.github.rafaelpeinado.agendaapi.model.repository;


import io.github.rafaelpeinado.agendaapi.model.entity.Contato;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContatoRepository extends JpaRepository<Contato, Integer> {

}
