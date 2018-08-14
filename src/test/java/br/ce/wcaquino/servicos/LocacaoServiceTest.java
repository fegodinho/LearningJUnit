package br.ce.wcaquino.servicos;


import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;

public class LocacaoServiceTest {
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testeLocacao() throws Exception {
		
		//cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1",2,5.0);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filme);
			
			//verificacao
			//assertEquals(5.0,locacao.getValor(), 0.01);
			//assertThat(locacao.getValor(),is(5.0));
			//assertThat(locacao.getValor(),is(equalTo(5.0)));
			error.checkThat(locacao.getValor(),is(equalTo(5.0)));
			//assertThat(locacao.getValor(),is(not(6.0)));
			
			//assertTrue(isMesmaData(locacao.getDataLocacao(), new Date())); //ou
			//assertThat(isMesmaData(locacao.getDataLocacao(), new Date()),is(true));
			error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()),is(true));
			//assertTrue(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1))); //ou
			//assertThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
			error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
					
	}
	
	@Test(expected=Exception.class)
	public void testeLocacao_filmeSemEstoque() throws Exception {
		//cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1",0,5.0);
				
		//acao
		service.alugarFilme(usuario, filme);		
	}
	
	@Test
	public void testeLocacao_filmeSemEstoque_2(){
		//cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1",0,5.0);
				
		//acao
		try {
			service.alugarFilme(usuario, filme);
			Assert.fail("Deveria ter lancado uma excecao");
		} catch (Exception e) {
			assertThat(e.getMessage(), is("Filme sem estoque"));
		}
	}
	
	@Test
	public void testeLocacao_filmeSemEstoque_3() throws Exception {
		//cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1",0,5.0);
		
		exception.expect(Exception.class);
		exception.expectMessage("Filme sem estoque");
				
		//acao
		service.alugarFilme(usuario, filme);
		
		
	}

}
