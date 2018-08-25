package br.ce.wcaquino.servicos;


import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.daos.LocacaoDAOFake;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import buildermaster.BuilderMaster;

public class LocacaoServiceTest {
	
	private LocacaoService service;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		service = new LocacaoService();
		LocacaoDAO dao = Mockito.mock(LocacaoDAO.class);
		service.setLocacaoDAO(dao);
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
				
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
	public void deveDevovlerNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		//verificacao
		//assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY)); ou
		assertThat(retorno.getDataRetorno(), caiNumaSegunda());		
	}
	
	public static void main(String[] args) {
		new BuilderMaster().gerarCodigoClasse(Locacao.class);
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
