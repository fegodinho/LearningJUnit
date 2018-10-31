package br.ce.wcaquino.servicos;


import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static br.ce.wcaquino.utils.DataUtils.obterData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class, DataUtils.class})
public class LocacaoServiceTest {
	
	@InjectMocks
	private LocacaoService service;
	
	@Mock
	private LocacaoDAO dao;
	@Mock
	private SPCService spc;
	@Mock
	private EmailService email;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		//Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(obterData(28, 4, 2017));
				
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
			
			//verificacao
			//assertEquals(5.0,locacao.getValor(), 0.01);
			//assertThat(locacao.getValor(),is(5.0));
			//assertThat(locacao.getValor(),is(equalTo(5.0)));
			error.checkThat(locacao.getValor(),is(equalTo(5.0)));
			//assertThat(locacao.getValor(),is(not(6.0)));
			
			//assertTrue(isMesmaData(locacao.getDataLocacao(), new Date())); //ou
			//assertThat(isMesmaData(locacao.getDataLocacao(), new Date()),is(true));
			//error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()),is(true));
			error.checkThat(locacao.getDataLocacao(), ehHoje());
			error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(),DataUtils.obterData(29, 4, 2017)), is(true));
			error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(),DataUtils.obterData(28, 4, 2017)), is(true));
			//assertTrue(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1))); //ou
			//assertThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
			//error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
			error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
					
	}
	
	@Test(expected=FilmeSemEstoqueException.class) //@Test(expected=Exception.class)
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());
				
		//acao
		service.alugarFilme(usuario, filmes);		
	}
	
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		//cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
				
		//acao
		try {
			service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
		}	
	}
	
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = umUsuario().agora();
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
								
		//acao
		service.alugarFilme(usuario, null);			
	}
	
	@Test
	public void deveDevovlerNaSegundaAoAlugarNoSabado() throws Exception {
		//Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(obterData(29, 4, 2017));
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		//verificacao
		//assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY)); ou
		assertThat(retorno.getDataRetorno(), caiNumaSegunda());
		PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
				
		//acao
		try {
			service.alugarFilme(usuario, filmes);
			//verificacao
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuário Negativado"));
		}
		
		verify(spc).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		//cenario
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("Usuario em dia").agora();
		Usuario usuario3 = umUsuario().comNome("Outro atrasado").agora();
		List<Locacao> locacoes = Arrays.asList(
				umLocacao().atrasado().comUsuario(usuario).agora(),
				umLocacao().atrasado().comUsuario(usuario3).agora(),
				umLocacao().atrasado().comUsuario(usuario3).agora(),
				umLocacao().comUsuario(usuario2).agora());
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);		
		
		//acao
		service.notificarAtrasos();
		
		//verificacao
		verify(email,times(3)).notificarAtraso(Mockito.any(Usuario.class));
		verify(email).notificarAtraso(usuario);
		verify(email, Mockito.atLeast(2)).notificarAtraso(usuario3);
		verify(email, never()).notificarAtraso(usuario2);
		verifyNoMoreInteractions(email);		
	}
	
	@Test
	public void deveTratarErroNoSPC () throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		Mockito.when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falhra catastrófica!"));
		
		//verificacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas com SPC, tente novamente");
		
		//acao
		service.alugarFilme(usuario, filmes);		
	}
	
	@Test
	public void deveProrrogarUmaLocacao() {
		//cenario
		Locacao locacao = umLocacao().agora();
		
		//acao
		service.prorrogarLocacao(locacao, 3);
		
		//verificacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(3));
	}
	
//	@Test
//	public void testeLocacao_filmeSemEstoque_2(){
//		//cenario
//		LocacaoService service = new LocacaoService();
//		Usuario usuario = new Usuario("Usuario 1");
//		Filme filme = new Filme("Filme 1",0,5.0);
//				
//		//acao
//		try {
//			service.alugarFilme(usuario, filme);
//			Assert.fail("Deveria ter lancado uma excecao");
//		} catch (Exception e) {
//			assertThat(e.getMessage(), is("Filme sem estoque"));
//		}
//	}
//	
//	@Test
//	public void testeLocacao_filmeSemEstoque_3() throws Exception {
//		//cenario
//		LocacaoService service = new LocacaoService();
//		Usuario usuario = new Usuario("Usuario 1");
//		Filme filme = new Filme("Filme 1",0,5.0);
//		
//		exception.expect(Exception.class);
//		exception.expectMessage("Filme sem estoque");
//				
//		//acao
//		service.alugarFilme(usuario, filme);		
//	}

}
