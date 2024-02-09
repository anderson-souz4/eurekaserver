package com.agrotis.trainees.crud.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.agrotis.trainees.crud.dtos.ParceiroNegocioDto;
import com.agrotis.trainees.crud.entity.ParceiroNegocio;
import com.agrotis.trainees.crud.repository.ParceiroNegocioRepository;
import com.agrotis.trainees.crud.service.exceptions.EntidadeNaoEncontradaException;
import com.agrotis.trainees.crud.utils.DtoUtils;

@Service
public class ParceiroNegocioService {

    private static final Logger LOG = LoggerFactory.getLogger(ParceiroNegocioService.class);

    private final ParceiroNegocioRepository repository;

    public ParceiroNegocioService(ParceiroNegocioRepository repository) {
        this.repository = repository;
    }

    public ParceiroNegocioDto salvar(ParceiroNegocioDto negocio) {
        ParceiroNegocio entidade = DtoUtils.converteParaEntidade(negocio);
        repository.save(entidade);
        LOG.info("Salvando Parceiro de Negocio {}", negocio.getNome());
        return DtoUtils.converteParaDto(entidade);

    }

    public ParceiroNegocioDto buscarPorId(Integer id) {
        ParceiroNegocio parceiroPorId = repository.findById(id)
                        .orElseThrow(() -> new EntidadeNaoEncontradaException("Entidade não encontrada com o ID: " + id));
        return DtoUtils.converteParaDto(parceiroPorId);
    }

    public List<ParceiroNegocioDto> listarTodos() {
        return repository.findAll()
                        .stream()
                        .map(DtoUtils::converteParaDto)
                        .collect(Collectors.toList());
    }

    public ParceiroNegocioDto buscarPorNome(String nome) {
        return repository.findByNome(nome).map(p -> DtoUtils.converteParaDto(p)).orElseThrow(() -> {
            LOG.info("Não foi possível encontrar o parceiro de negócio pelo nome {}", nome);
            return new EntidadeNaoEncontradaException("Parceiro de negócio com o nome '" + nome + "' não encontrado");
        });
    }

    public void deletarPorId(Integer id) {
        repository.findById(id).map(entidade -> {
            repository.deleteById(id);
            LOG.info("Deletado com sucesso");
            return entidade;
        }).orElseThrow(() -> new EntidadeNaoEncontradaException("Entidade com o ID " + id + " não encontrada"));
    }

    public ParceiroNegocio atualizar(Integer id, ParceiroNegocio negocio) {
        return repository.findById(id).map(parceiroExistente -> {
            parceiroExistente.setNome(negocio.getNome());
            parceiroExistente.setInscricaoFiscal(negocio.getInscricaoFiscal());
            parceiroExistente.setEndereco(negocio.getEndereco());
            parceiroExistente.setTelefone(negocio.getTelefone());
            return repository.save(parceiroExistente);
        }).orElseThrow(() -> {
            LOG.info("Não foi possível encontrar o parceiro de negócio pelo ID {}", id);
            return new EntidadeNaoEncontradaException("Parceiro de negócio com o ID " + id + " não encontrado");
        });
    }

}
