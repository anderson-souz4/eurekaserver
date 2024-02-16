package com.agrotis.trainees.crud.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.agrotis.trainees.crud.dtos.CabecalhoNotaDto;
import com.agrotis.trainees.crud.dtos.ItemNotaDto;
import com.agrotis.trainees.crud.dtos.ParceiroNegocioDto;
import com.agrotis.trainees.crud.dtos.ProdutoDto;
import com.agrotis.trainees.crud.entity.CabecalhoNota;
import com.agrotis.trainees.crud.entity.ItemNota;
import com.agrotis.trainees.crud.entity.ParceiroNegocio;
import com.agrotis.trainees.crud.entity.Produto;
import com.agrotis.trainees.crud.entity.enums.TipoNota;
import com.agrotis.trainees.crud.repository.NotaFiscalItemRepository;
import com.agrotis.trainees.crud.service.exceptions.EntidadeNaoEncontradaException;
import com.agrotis.trainees.crud.utils.DtoUtils;
import com.agrotis.trainees.crud.utils.ValidacaoUtils;

@Service
public class ItemNotaService {

    private static final Logger LOG = LoggerFactory.getLogger(ItemNotaService.class);

    private final NotaFiscalItemRepository repository;
    private final ProdutoService produtoService;
    private final CabecalhoNotaService cabecalhoNotaService;
    private final ParceiroNegocioService parceiroNegocioService;

    public ItemNotaService(NotaFiscalItemRepository repository, ProdutoService produtoService,
                    CabecalhoNotaService cabecalhoNotaService,ParceiroNegocioService parceiroNegocioService) {
        this.repository = repository;
        this.produtoService = produtoService;
        this.cabecalhoNotaService = cabecalhoNotaService;
        this.parceiroNegocioService = parceiroNegocioService;
    }

    @Transactional
    public ItemNotaDto salvar(ItemNotaDto dto) {
        ItemNota entidade = DtoUtils.converteParaEntidade(dto);

        // Persist the transient entities and ensure they are properly managed
        CabecalhoNota cabecalhoNota = entidade.getCabecalhoNota();
        Produto produto = entidade.getProduto();
        ParceiroNegocio parceiroNegocio = cabecalhoNota.getParceiroNegocio();

        // Save or update the transient entities
        CabecalhoNotaDto cabecalhoNotaDtoSalvo = cabecalhoNotaService.salvar(DtoUtils.converteParaDto(cabecalhoNota));
        ProdutoDto produtoDtoSalvo  = produtoService.salvar(DtoUtils.converteParaDto(produto));
        ParceiroNegocioDto parceiroNegocioDtoSalvo = parceiroNegocioService.salvar(DtoUtils.converteParaDto(parceiroNegocio));

        // Ensure the transient entities are properly initialized with IDs after saving
        entidade.setCabecalhoNota(DtoUtils.converteParaEntidade(cabecalhoNotaDtoSalvo));
        entidade.setProduto(DtoUtils.converteParaEntidade(produtoDtoSalvo));

        // Set the updated ParceiroNegocio back to the CabecalhoNota
        cabecalhoNota.setParceiroNegocio(DtoUtils.converteParaEntidade(parceiroNegocioDtoSalvo));

        // Perform other operations
        calcularValorTotal(entidade);
        adicionarValorTotalCabecalho(entidade);
        atualizarEstoque(entidade);

        // Save the ItemNota entity
        entidade = repository.save(entidade);

        return DtoUtils.converteParaDto(entidade);
    }

    public List<ItemNota> buscarTodos() {
        return repository.findAll();
    }

    public ItemNota buscarPorId(Integer id) {
        return repository.findById(id)
                        .orElseThrow(() -> new EntidadeNaoEncontradaException("Entidade não encontrada com o ID: " + id));
    }

    public ItemNota atualizar(Integer id, ItemNota notaFiscalItem) {
        return repository.findById(id).map(itemNotaExistente -> {
            itemNotaExistente.setCabecalhoNota(notaFiscalItem.getCabecalhoNota());
            itemNotaExistente.setPrecoUnitario(notaFiscalItem.getPrecoUnitario());
            itemNotaExistente.setProduto(notaFiscalItem.getProduto());
            itemNotaExistente.setQuantidade(notaFiscalItem.getQuantidade());
            itemNotaExistente.setValorTotal(notaFiscalItem.getValorTotal());
            return repository.save(itemNotaExistente);
        }).orElseThrow(() -> new EntidadeNaoEncontradaException("Entidade não encontrada com o ID: " + id));
    }

    public void deletarPorId(Integer id) {
        repository.findById(id).map(entidade -> {
            repository.deleteById(id);
            LOG.info("Deletado com sucesso");
            return entidade;
        }).orElseThrow(() -> new EntidadeNaoEncontradaException("Entidade não encontrada com o ID: " + id));

    }

    public void calcularValorTotal(ItemNota notaFiscalItem) {
        Integer quantidade = notaFiscalItem.getQuantidade();
        Double precoUnitario = notaFiscalItem.getPrecoUnitario();
        if (quantidade != null && precoUnitario != null) {
            Double valorTotal = quantidade * precoUnitario;
            notaFiscalItem.setValorTotal(valorTotal);
        }
    }

    private void atualizarEstoque(ItemNota itemNota) {
        Produto produto = itemNota.getProduto();
        Integer quantidade = itemNota.getQuantidade();
        TipoNota notaFiscalTipo = itemNota.getCabecalhoNota().getNotaFiscalTipo();
        Integer quantidadeProduto = produto.getQuantidadeEstoque();

        ValidacaoUtils.validarQuantidadeNaoNegativa(quantidade);

        if (notaFiscalTipo == TipoNota.SAIDA) {
            ValidacaoUtils.validarQuantidadeEstoqueSuficiente(quantidadeProduto, quantidade);
        }

        if (notaFiscalTipo == TipoNota.ENTRADA) {
            produto.setQuantidadeEstoque(quantidadeProduto + quantidade);
        } else {
            produto.setQuantidadeEstoque(quantidadeProduto - quantidade);
        }

        produtoService.salvar(DtoUtils.converteParaDto(produto));
    }

    private void adicionarValorTotalCabecalho(ItemNota item) {
        CabecalhoNota cabecalho = item.getCabecalhoNota();
        Double valorTotalItem = item.getValorTotal();
        cabecalho.setValorTotal(valorTotalItem);
        
    }


}
