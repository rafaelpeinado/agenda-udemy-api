package io.github.rafaelpeinado.agendaapi.api.rest;

import io.github.rafaelpeinado.agendaapi.model.entity.Contato;
import io.github.rafaelpeinado.agendaapi.model.repository.ContatoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contatos")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ContatoController {

    // final que é para exigir ele no construtor e injetar depednencias
    private final ContatoRepository repository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Contato save(@RequestBody Contato contato) {
        return repository.save(contato);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        repository.deleteById(id);
    }

    @GetMapping
    public Page<Contato> list(
            @RequestParam(value = "page", defaultValue = "0") Integer pagina,
            @RequestParam(value = "size", defaultValue = "10") Integer tamanhoPagina) {
        Sort sort = Sort.by(Sort.Direction.ASC, "nome");
        PageRequest pageRequest = PageRequest.of(pagina, tamanhoPagina, sort);
        return repository.findAll(pageRequest);
    }

    // para atualização parcial
    @PatchMapping("{id}/favorito")
    public void favorite(@PathVariable Integer id) {
        repository.findById(id).ifPresent(contato -> {
            boolean favorito = contato.getFavorito() == Boolean.TRUE;
            contato.setFavorito(!favorito);
            repository.save(contato);
        });
    }

    // domínio/api/contatos/1/foto
    @PutMapping("{id}/foto")
    public byte[] addPhoto(@PathVariable Integer id, @RequestParam("foto") Part file) {
        Optional<Contato> contato = repository.findById(id);
        return contato.map(c -> {
            try {
                InputStream is = file.getInputStream();
                byte[] bytes = new byte[(int) file.getSize()];
                IOUtils.readFully(is, bytes);
                c.setFoto(bytes);
                repository.save(c);
                is.close();
                return bytes;
            } catch (IOException e) {
                return null;
            }
        }).orElse(null);
    }
}
