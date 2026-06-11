const porId = (id) => document.getElementById(id);
const numero = new Intl.NumberFormat("pt-BR");

const estado = {
    integrantes: [], times: [], consulta: "integrante-mais-usado"
};

const consultas = {
    "integrante-mais-usado": "Integrante mais escalado",
    "integrantes-do-time-mais-recorrente": "Formação recorrente",
    "funcao-mais-recorrente": "Função recorrente",
    "clube-mais-recorrente": "Clube recorrente",
    "contagem-de-clubes": "Participações por clube",
    "contagem-por-funcao": "Integrantes por função",
    "time-da-data": "Time por data"
};

let temporizadorMensagem;

function escapar(valor) {
    return String(valor ?? "")
        .replace(/&/g, "&amp;").replace(/</g, "&lt;")
        .replace(/>/g, "&gt;").replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function dataBr(data) {
    if (!data) return "Data não informada";
    const [ano, mes, dia] = data.split("-");
    return dia && mes && ano ? `${dia}/${mes}/${ano}` : data;
}

function dataIso(dataBrasileira, obrigatoria = false) {
    const valor = String(dataBrasileira || "").trim();
    if (!valor) {
        if (obrigatoria) throw new Error("Informe a data no formato dd/mm/aaaa.");
        return "";
    }

    const correspondencia = valor.match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
    if (!correspondencia) throw new Error(`A data "${valor}" deve estar no formato dd/mm/aaaa.`);

    const [, dia, mes, ano] = correspondencia;
    const data = new Date(Number(ano), Number(mes) - 1, Number(dia));
    const valida = data.getFullYear() === Number(ano) && data.getMonth() === Number(mes) - 1 && data.getDate() === Number(dia);

    if (!valida) throw new Error(`A data "${valor}" não é válida.`);
    return `${ano}-${mes}-${dia}`;
}

function aplicarMascaraData(valor) {
    const numeros = valor.replace(/\D/g, "").slice(0, 8);
    if (numeros.length <= 2) return numeros;
    if (numeros.length <= 4) return `${numeros.slice(0, 2)}/${numeros.slice(2)}`;
    return `${numeros.slice(0, 2)}/${numeros.slice(2, 4)}/${numeros.slice(4)}`;
}

function valorObrigatorio(campo, mensagem) {
    const valor = campo.value.trim();
    if (!valor) {
        campo.focus();
        throw new Error(mensagem);
    }
    return valor;
}

function iniciais(nome) {
    return String(nome).trim().split(/\s+/).slice(0, 2)
        .map((parte) => parte[0].toLocaleUpperCase("pt-BR")).join("");
}

async function requisitar(url, opcoes = {}) {
    let resposta;
    try {
        resposta = await fetch(url, {headers: {"Content-Type": "application/json"}, ...opcoes});
    } catch (erro) {
        throw new Error("Não foi possível conectar ao servidor.");
    }
    if (!resposta.ok) {
        const corpo = await resposta.json().catch(() => null);
        const mensagens = corpo && Array.isArray(corpo.mensagens) ? corpo.mensagens : [];
        throw new Error(mensagens.join(" ") || (resposta.status === 404 ? "Nenhum resultado foi encontrado." : "Não foi possível concluir a operação."));
    }
    return resposta.status === 204 ? null : resposta.json();
}

function notificar(texto, tipo = "sucesso") {
    clearTimeout(temporizadorMensagem);
    const mensagem = porId("mensagem");
    const modalAberto = document.querySelector("dialog[open]");
    const destino = modalAberto ? modalAberto.querySelector("form") : document.body;
    if (mensagem.parentElement !== destino) {
        destino.appendChild(mensagem);
    }
    mensagem.classList.toggle("no-modal", Boolean(modalAberto));
    porId("texto-mensagem").textContent = texto;
    mensagem.classList.toggle("erro", tipo === "erro");
    mensagem.classList.add("visivel");
    temporizadorMensagem = setTimeout(fecharNotificacao, 4500);
}

function fecharNotificacao() {
    const mensagem = porId("mensagem");
    mensagem.className = "notificacao";
    if (mensagem.parentElement !== document.body) {
        document.body.appendChild(mensagem);
    }
}

function alternarCarregamento(botao, ativo, texto) {
    if (!botao.dataset.original) botao.dataset.original = botao.textContent.trim();
    botao.disabled = ativo;
    const alvo = botao.querySelector("span") || botao;
    alvo.textContent = ativo ? texto : botao.dataset.original;
}

function abrirTela(nome) {
    document.querySelectorAll("[data-painel]").forEach((painel) => painel.classList.toggle("ativa", painel.dataset.painel === nome));
    document.querySelectorAll("[data-tela]").forEach((item) => item.classList.toggle("ativo", item.dataset.tela === nome));
    const titulos = {inicio: "Visão geral", integrantes: "Integrantes", times: "Times", analises: "Análises"};
    porId("titulo-atual").textContent = titulos[nome];
    fecharMenu();
    window.scrollTo({top: 0, behavior: "smooth"});
}

function abrirModal(id) {
    const modal = porId(id);
    if (id === "modal-time" && estado.integrantes.length === 0) {
        abrirModal("modal-integrante");
        notificar("Cadastre ao menos um integrante antes de montar um time.", "erro");
        return;
    }
    modal.showModal();
    const primeiroCampo = modal.querySelector("input:not([type=checkbox])");
    setTimeout(() => primeiroCampo && primeiroCampo.focus(), 100);
}

function fecharModal(modal) {
    modal.close();
    const mensagem = porId("mensagem");
    if (modal.contains(mensagem)) {
        mensagem.className = "notificacao";
        document.body.appendChild(mensagem);
    }
}

function abrirMenu() {
    porId("barra-lateral").classList.add("aberta");
    porId("fundo-menu").classList.add("visivel");
}

function fecharMenu() {
    porId("barra-lateral").classList.remove("aberta");
    porId("fundo-menu").classList.remove("visivel");
}

function abrirCalendario(seletor) {
    if (typeof seletor.showPicker === "function") {
        try {
            seletor.showPicker();
        } catch (erro) {
            seletor.focus();
        }
    } else {
        seletor.focus();
        seletor.click();
    }
}

function prepararCalendario(seletor) {
    const campoVisivel = porId(seletor.dataset.campoData);
    try {
        seletor.value = dataIso(campoVisivel.value);
    } catch (erro) {
        seletor.value = "";
    }
    abrirCalendario(seletor);
}

function estadoVazio(texto) {
    return `<div class="estado-vazio"><p>${escapar(texto)}</p></div>`;
}

function atualizarIndicadores() {
    const clubes = new Set(estado.times.map((time) => time.nomeDoClube));
    porId("total-integrantes").textContent = numero.format(estado.integrantes.length);
    porId("total-times").textContent = numero.format(estado.times.length);
    porId("total-clubes").textContent = numero.format(clubes.size);
    porId("selo-integrantes").textContent = numero.format(estado.integrantes.length);
    porId("selo-times").textContent = numero.format(estado.times.length);
}

function renderizarRecentes() {
    const recentes = [...estado.times].sort((a, b) => b.data.localeCompare(a.data)).slice(0, 4);
    porId("times-recentes").innerHTML = recentes.length ? recentes.map((time) => `
        <div class="item-recente">
            <span class="emblema-clube">${escapar(iniciais(time.nomeDoClube))}</span>
            <div><strong>${escapar(time.nomeDoClube)}</strong><small>${time.integrantes.length} integrantes</small></div>
            <span class="data">${escapar(dataBr(time.data))}</span>
        </div>`).join("") : estadoVazio("Nenhum time montado. Use a ação acima para começar.");
}

function renderizarIntegrantes(filtro = "") {
    const termo = filtro.trim().toLocaleLowerCase("pt-BR");
    const itens = estado.integrantes.filter((item) => `${item.nome} ${item.funcao}`.toLocaleLowerCase("pt-BR").includes(termo));
    porId("quantidade-integrantes").textContent = `${numero.format(itens.length)} ${itens.length === 1 ? "integrante" : "integrantes"}`;
    porId("lista-integrantes").innerHTML = itens.length ? itens.map((item) => `
        <article class="cartao-integrante">
            <span class="avatar">${escapar(iniciais(item.nome))}</span>
            <div><strong>${escapar(item.nome)}</strong><small>${escapar(item.funcao)}</small></div>
        </article>`).join("") : estadoVazio(termo ? "Nenhum integrante corresponde à busca." : "Nenhum integrante cadastrado.");

    porId("opcoes-integrantes").innerHTML = estado.integrantes.length ? estado.integrantes.map((item) => `
        <label class="opcao">
            <input name="integrante" type="checkbox" value="${item.id}">
            <span>${escapar(item.nome)}<small>${escapar(item.funcao)}</small></span>
        </label>`).join("") : estadoVazio("Cadastre integrantes primeiro.");
}

function renderizarTimes(filtro = "") {
    const termo = filtro.trim().toLocaleLowerCase("pt-BR");
    const itens = estado.times.filter((time) => `${time.nomeDoClube} ${time.integrantes.map((item) => item.nome).join(" ")}`
        .toLocaleLowerCase("pt-BR").includes(termo));
    porId("quantidade-times").textContent = `${numero.format(itens.length)} ${itens.length === 1 ? "time" : "times"}`;
    porId("lista-times").innerHTML = itens.length ? itens.map((time) => {
        const nomes = time.integrantes.map((item) => item.nome);
        return `<article class="cartao-time">
            <div class="topo-time"><div><h3>${escapar(time.nomeDoClube)}</h3><small>${time.integrantes.length} integrantes</small></div><span class="data-time">${escapar(dataBr(time.data))}</span></div>
            <div class="linha"></div><p class="rotulo-formacao">Formação</p>
            <div class="avatares">${nomes.slice(0, 6).map((nome) => `<span class="avatar-mini" title="${escapar(nome)}">${escapar(iniciais(nome))}</span>`).join("")}</div>
            <p class="nomes-formacao">${escapar(nomes.join(", ") || "Sem integrantes")}</p>
        </article>`;
    }).join("") : estadoVazio(termo ? "Nenhum time corresponde à busca." : "Nenhum time montado.");
}

async function carregarDados() {
    [estado.integrantes, estado.times] = await Promise.all([requisitar("/api/integrantes"), requisitar("/api/times")]);
    atualizarIndicadores();
    renderizarRecentes();
    renderizarIntegrantes(porId("busca-integrantes").value);
    renderizarTimes(porId("busca-times").value);
}

function selecionarConsulta(caminho) {
    estado.consulta = caminho;
    document.querySelectorAll("[data-consulta]").forEach((botao) => botao.classList.toggle("ativa", botao.dataset.consulta === caminho));
    porId("nome-consulta").textContent = consultas[caminho];
    const dataUnica = caminho === "time-da-data";
    porId("filtros-periodo").hidden = dataUnica;
    porId("filtro-data-unica").hidden = !dataUnica;
    porId("resultado").innerHTML = `<div class="estado-consulta"><span>↗</span><h3>Pronto para analisar</h3><p>${dataUnica ? "Selecione uma data para localizar o time." : "Defina o período ou consulte todo o histórico."}</p></div>`;
}

function listaResultado(itens) {
    return `<div class="lista-resultado">${itens.map(([chave, valor]) => `<div class="item-resultado"><span>${escapar(chave)}</span><b>${escapar(numero.format(valor))}</b></div>`).join("")}</div>`;
}

function mostrarResultado(caminho, dados) {
    if (dados == null || (Array.isArray(dados) && !dados.length) || (typeof dados === "object" && !Array.isArray(dados) && !Object.keys(dados).length)) {
        porId("resultado").innerHTML = `<div class="estado-consulta"><span>○</span><h3>Nenhum dado encontrado</h3><p>Não existem registros para os filtros informados.</p></div>`;
        return;
    }
    let html;
    if (caminho === "integrante-mais-usado") {
        html = `<p class="marcador">Integrante mais escalado</p><div class="destaque">${escapar(dados.nome)}</div><p>Função: ${escapar(dados.funcao)}</p>`;
    } else if (caminho === "integrantes-do-time-mais-recorrente") {
        html = `<p class="marcador">Formação recorrente</p><div class="destaque">${dados.length} integrantes</div>${listaResultado(dados.map((nome, i) => [nome, i + 1]))}`;
    } else if (caminho === "funcao-mais-recorrente") {
        html = `<p class="marcador">Função mais recorrente</p><div class="destaque">${escapar(dados["Função"] || "Não informada")}</div>`;
    } else if (caminho === "clube-mais-recorrente") {
        html = `<p class="marcador">Clube mais recorrente</p><div class="destaque">${escapar(dados.clube || "Não informado")}</div>`;
    } else if (caminho === "contagem-de-clubes") {
        html = `<p class="marcador">Participações por clube</p><div class="destaque">${Object.keys(dados).length} clubes</div>${listaResultado(Object.entries(dados))}`;
    } else if (caminho === "contagem-por-funcao") {
        html = `<p class="marcador">Integrantes por função</p><div class="destaque">${Object.keys(dados).length} funções</div>${listaResultado(Object.entries(dados))}`;
    } else {
        const nomes = dados.integrantes;
        html = `<p class="marcador">Time de ${escapar(dataBr(dados.data))}</p><div class="destaque">${escapar(dados.clube)}</div><p>${nomes.length} integrantes</p>${listaResultado(nomes.map((nome, i) => [nome, i + 1]))}`;
    }
    porId("resultado").innerHTML = `<div class="resultado-conteudo">${html}</div>`;
}

async function executarAnalise() {
    const botao = porId("executar-analise");
    const parametros = new URLSearchParams();
    try {
        if (estado.consulta === "time-da-data") {
            const data = dataIso(porId("data-consulta").value, true);
            parametros.set("data", data);
        } else {
            const inicio = dataIso(porId("data-inicial").value);
            const fim = dataIso(porId("data-final").value);
            if (inicio && fim && inicio > fim) throw new Error("A data inicial não pode ser posterior à data final.");
            if (inicio) parametros.set("dataInicial", inicio);
            if (fim) parametros.set("dataFinal", fim);
        }
        alternarCarregamento(botao, true, "Analisando...");
        porId("resultado").innerHTML = `<div class="carregando" aria-label="Carregando"></div>`;
        mostrarResultado(estado.consulta, await requisitar(`/api/processamento/${estado.consulta}?${parametros}`));
    } catch (erro) {
        porId("resultado").innerHTML = `<div class="estado-consulta"><span>!</span><h3>Não foi possível analisar</h3><p>${escapar(erro.message)}</p></div>`;
        notificar(erro.message, "erro");
    } finally {
        alternarCarregamento(botao, false, "Analisando...");
    }
}

document.querySelectorAll("[data-tela]").forEach((botao) => botao.addEventListener("click", () => abrirTela(botao.dataset.tela)));
document.querySelectorAll("[data-ir-para]").forEach((botao) => botao.addEventListener("click", () => abrirTela(botao.dataset.irPara)));
document.querySelectorAll("[data-abrir-modal]").forEach((botao) => botao.addEventListener("click", () => abrirModal(botao.dataset.abrirModal)));
document.querySelectorAll("[data-fechar-modal]").forEach((botao) => botao.addEventListener("click", () => fecharModal(botao.closest("dialog"))));
document.querySelectorAll(".modal").forEach((modal) => modal.addEventListener("click", (evento) => {
    if (evento.target === modal) fecharModal(modal);
}));
document.querySelectorAll("[data-abrir-calendario]").forEach((botao) => botao.addEventListener("click", () => {
    const seletor = porId(botao.dataset.abrirCalendario);
    prepararCalendario(seletor);
}));
document.querySelectorAll("[data-campo-data]").forEach((seletor) => seletor.addEventListener("change", () => {
    porId(seletor.dataset.campoData).value = dataBr(seletor.value);
}));
document.querySelectorAll(".campo-data > input:not(.seletor-data)").forEach((campo) => {
    campo.addEventListener("input", () => campo.value = aplicarMascaraData(campo.value));
    campo.addEventListener("click", () => {
        const seletor = campo.parentElement.querySelector(".seletor-data");
        prepararCalendario(seletor);
    });
});
document.querySelectorAll("[data-consulta]").forEach((botao) => botao.addEventListener("click", () => selecionarConsulta(botao.dataset.consulta)));

porId("abrir-menu").addEventListener("click", abrirMenu);
porId("fundo-menu").addEventListener("click", fecharMenu);
porId("fechar-mensagem").addEventListener("click", fecharNotificacao);
porId("busca-integrantes").addEventListener("input", (evento) => renderizarIntegrantes(evento.target.value));
porId("busca-times").addEventListener("input", (evento) => renderizarTimes(evento.target.value));
porId("executar-analise").addEventListener("click", executarAnalise);

porId("form-integrante").addEventListener("submit", async (evento) => {
    evento.preventDefault();
    const botao = evento.submitter || evento.currentTarget.querySelector('[type="submit"]');
    try {
        const nome = valorObrigatorio(porId("nome"), "Informe o nome do integrante.");
        const funcao = valorObrigatorio(porId("funcao"), "Informe a função do integrante.");
        alternarCarregamento(botao, true, "Cadastrando...");
        await requisitar("/api/integrantes", {
            method: "POST", body: JSON.stringify({
                nome, funcao
            })
        });
        evento.target.reset();
        fecharModal(porId("modal-integrante"));
        await carregarDados();
        notificar("Integrante cadastrado com sucesso.");
    } catch (erro) {
        notificar(erro.message, "erro");
    } finally {
        alternarCarregamento(botao, false, "Cadastrando...");
    }
});

porId("form-time").addEventListener("submit", async (evento) => {
    evento.preventDefault();
    const botao = evento.submitter || evento.currentTarget.querySelector('[type="submit"]');
    const integrantesIds = [...document.querySelectorAll("[name=integrante]:checked")].map((item) => Number(item.value));
    try {
        const nomeDoClube = valorObrigatorio(porId("clube"), "Informe o nome do clube.");
        const data = dataIso(porId("data").value, true);
        if (!integrantesIds.length) {
            throw new Error("Selecione ao menos um integrante para montar o time.");
        }
        alternarCarregamento(botao, true, "Montando...");
        await requisitar("/api/times", {
            method: "POST", body: JSON.stringify({
                nomeDoClube, data, integrantesIds
            })
        });
        evento.target.reset();
        porId("data-calendario").value = "";
        fecharModal(porId("modal-time"));
        await carregarDados();
        abrirTela("times");
        notificar("Time montado com sucesso.");
    } catch (erro) {
        notificar(erro.message, "erro");
    } finally {
        alternarCarregamento(botao, false, "Montando...");
    }
});

selecionarConsulta(estado.consulta);
carregarDados().catch((erro) => notificar(erro.message, "erro"));
