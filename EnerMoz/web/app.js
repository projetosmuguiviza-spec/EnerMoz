const STORAGE_USER = "enermoz_user";
const STORAGE_ACCOUNTS = "enermoz_accounts";
const STORAGE_SESSION = "enermoz_session_email";
const STORAGE_DEVICES_PREFIX = "enermoz_devices_";

const EDM = {
  fixed: 233.37,
  tiers: [
    { max: 300, price: 6.00, label: "0-300 kWh" },
    { max: 500, price: 8.49, label: "301-500 kWh" },
    { max: Infinity, price: 8.91, label: "Acima de 500 kWh" }
  ]
};

const categories = [
  ["ELETRONICO", "Electrónico", 120],
  ["ELETRODOMESTICO", "Electrodoméstico", 250],
  ["ILUMINACAO", "Iluminação", 15],
  ["CLIMATIZACAO", "Climatização", 900],
  ["INFORMATICA", "Informática", 160],
  ["COMUNICACAO", "Comunicação", 20],
  ["ENTRETENIMENTO", "Entretenimento", 120],
  ["BOMBEAMENTO", "Bombeamento", 750],
  ["OUTRO", "Outro", 100]
];

const pages = {
  home: ["Painel de Energia", "Início"],
  devices: ["Aparelhos", "Registar Aparelhos."],
  consumption: ["Consumo", "Distribuição do consumo."],
  bill: ["Factura ", "Estimativa mensal."],
  alerts: ["Alertas", "Pontos que merecem atenção."],
  saving: ["Poupança", "Simule pequenas mudanças no uso diário."],
  reports: ["Relatórios", "Resumo geral."]
};

const authPages = ["login", "register", "recover"];
const icons = {
  home: "https://cdn-icons-png.flaticon.com/512/1946/1946436.png",
  devices: "https://cdn-icons-png.flaticon.com/512/3659/3659899.png",
  consumption: "https://cdn-icons-png.flaticon.com/512/1828/1828911.png",
  bill: "https://cdn-icons-png.flaticon.com/512/2921/2921222.png",
  alerts: "https://cdn-icons-png.flaticon.com/512/1827/1827370.png",
  saving: "https://cdn-icons-png.flaticon.com/512/3135/3135706.png",
  reports: "https://cdn-icons-png.flaticon.com/512/2991/2991112.png",
  brand: "https://cdn-icons-png.flaticon.com/512/702/702814.png"
};

const page = document.body.dataset.page;
const sessionEmail = localStorage.getItem(STORAGE_SESSION);
const accounts = loadAccounts();
const savedUser = JSON.parse(localStorage.getItem(STORAGE_USER) || "null");
const storedUser = sessionEmail
  ? accounts.find((account) => account.email === sessionEmail) || (savedUser?.email === sessionEmail ? savedUser : null)
  : null;
const state = {
  user: storedUser,
  devices: JSON.parse(localStorage.getItem(devicesKey(storedUser?.email)) || "[]"),
  charts: {}
};

function createDevice(name, category, hours, power, location) {
  return { name, category, categoryLabel: categoryName(category), hours, power, location, notes: "" };
}

function loadAccounts() {
  return JSON.parse(localStorage.getItem(STORAGE_ACCOUNTS) || "[]");
}

function saveAccounts(items) {
  localStorage.setItem(STORAGE_ACCOUNTS, JSON.stringify(items));
}

function normalizeEmail(email) {
  return email.trim().toLowerCase();
}

function devicesKey(email) {
  return `${STORAGE_DEVICES_PREFIX}${email || "guest"}`;
}

function categoryName(category) {
  return categories.find(([value]) => value === category)?.[1] || "Outro";
}

function saveDevices() {
  localStorage.setItem(devicesKey(state.user?.email), JSON.stringify(state.devices));
}

function monthly(device) {
  return (device.power * device.hours * 30) / 1000;
}

function totalConsumption() {
  return state.devices.reduce((sum, device) => sum + monthly(device), 0);
}

function tariff(consumption) {
  return EDM.tiers.find((tier) => consumption <= tier.max);
}

function bill() {
  const consumption = totalConsumption();
  if (consumption === 0) {
    return {
      consumption,
      tier: { price: 0, label: "Sem consumo" },
      energy: 0,
      fixed: 0,
      total: 0
    };
  }
  const tier = tariff(consumption);
  const energy = consumption * tier.price;
  return { consumption, tier, energy, fixed: EDM.fixed, total: energy + EDM.fixed };
}

function money(value) {
  return `${Math.round(value).toLocaleString("pt-MZ")} MT`;
}

function topDevices() {
  return [...state.devices].sort((a, b) => monthly(b) - monthly(a));
}

function deviceLevel(device) {
  const consumption = monthly(device);
  if (consumption >= 80) return ["bad", "Consumo elevado"];
  if (device.hours > 12 || consumption >= 35) return ["warn", "Atenção"];
  return ["good", "Eficiente"];
}

function estimatePower(name, category) {
  const text = name.toLowerCase();
  const known = [
    ["ar condicionado", 900],
    ["geleira", 180],
    ["tv", 120],
    ["bomba", 750],
    ["computador", 160],
    ["router", 12],
    ["ventoinha", 75],
    ["starlink", 75],
    ["ups", 100]
  ];
  const found = known.find(([key]) => text.includes(key));
  return found ? found[1] : categories.find(([value]) => value === category)?.[2] || 100;
}

function buildSidebar() {
  const sidebar = document.querySelector(".sidebar");
  if (!sidebar) return;
  const links = [
    ["home.html", "home", "Início"],
    ["aparelhos.html", "devices", "Aparelhos"],
    ["consumo.html", "consumption", "Consumo"],
    ["factura.html", "bill", "Factura"],
    ["alertas.html", "alerts", "Alertas"],
    ["poupanca.html", "saving", "Poupança"],
    ["relatorios.html", "reports", "Relatórios"]
  ];

  sidebar.innerHTML = `
    <div class="brand">
      <span class="brand-icon">
        <img src="${icons.brand}" alt="">
      </span>
      <div>
        <strong>EnerMoz</strong>
        <small>Energia Inteligente para para sua residencia</small>
      </div>
    </div>
    <nav class="nav-list">
      ${links.map(([href, key, label]) => `
        <a class="${page === key ? "active" : ""}" href="${href}">
          <img class="nav-icon" src="${icons[key]}" alt="">${label}
        </a>
      `).join("")}
    </nav>
    <div class="sidebar-note">
      <strong>${state.user.home}</strong>
      <span>${Math.round(totalConsumption())} kWh este mês</span>
    </div>
  `;
}

function buildTopbar() {
  const topbar = document.querySelector(".page-top");
  if (!topbar) return;
  const [title, subtitle] = pages[page] || pages.home;
  topbar.innerHTML = `
    <div class="page-title">
      <span class="small-label">${subtitle}</span>
      <h1>${title}</h1>
    </div>
    <div class="profile-box">
      <span>${state.user.home}</span>
      <strong>${state.user.name}</strong>
      <small>${formatDateTime(new Date())}</small>
      <button class="logout-button" id="logoutButton" type="button">Sair</button>
    </div>
  `;
  document.querySelector("#logoutButton")?.addEventListener("click", () => {
    localStorage.removeItem(STORAGE_SESSION);
    localStorage.removeItem(STORAGE_USER);
    window.location.href = "entrar.html";
  });
}

function renderHome() {
  const total = totalConsumption();
  const billData = bill();
  const top = topDevices()[0];
  setText("#homeDevices", state.devices.length);
  setText("#homeConsumption", `${Math.round(total)} kWh`);
  setText("#homeBill", money(billData.total));
  setText("#homeTopDevice", top ? top.name : "Sem dados");
  renderRanking("#homeRanking", 5);
  renderLineChart("homeChart");
}

function renderDevices() {
  const select = document.querySelector("#category");
  if (select && !select.children.length) {
    categories.forEach(([value, label]) => {
      const option = document.createElement("option");
      option.value = value;
      option.textContent = label;
      select.appendChild(option);
    });
  }

  const list = document.querySelector("#deviceList");
  if (!list) return;
  if (!state.devices.length) {
    list.innerHTML = `<div class="empty">Ainda não há aparelhos registados.</div>`;
    return;
  }

  list.innerHTML = topDevices().map((device) => {
    const index = state.devices.indexOf(device);
    const [className, label] = deviceLevel(device);
    return `
      <article class="device-card">
        <div>
          <h3>${device.name}</h3>
          <div class="device-meta">
            <span>${device.categoryLabel}</span>
            <span>${device.location || "Sem localização"}</span>
            <span>${monthly(device).toFixed(1)} kWh/mês</span>
            <span class="status ${className}">${label}</span>
          </div>
        </div>
        <div class="card-actions">
          <button class="card-button" data-edit="${index}" type="button">Editar</button>
          <button class="card-button remove" data-remove="${index}" type="button">Remover</button>
        </div>
      </article>
    `;
  }).join("");
}

function renderRanking(selector, limit) {
  const target = document.querySelector(selector);
  if (!target) return;
  if (!state.devices.length) {
    target.innerHTML = `<div class="empty">O ranking aparece depois de registar aparelhos.</div>`;
    return;
  }
  target.innerHTML = topDevices().slice(0, limit).map((device, index) => `
    <article class="rank-card">
      <span class="medal">${index + 1}º</span>
      <div>
        <h3>${device.name}</h3>
        <span class="device-meta">${device.categoryLabel}</span>
      </div>
      <strong>${monthly(device).toFixed(0)} kWh</strong>
    </article>
  `).join("");
}

function renderBill() {
  const rows = document.querySelector("#billRows");
  if (!rows) return;
  const data = bill();
  rows.innerHTML = `
    <div><span>Consumo</span><strong>${data.consumption.toFixed(1)} kWh</strong></div>
    <div><span>Escalão</span><strong>${data.tier.label}</strong></div>
    <div><span>Tarifa</span><strong>${data.tier.price.toFixed(2)} MT/kWh</strong></div>
    <div><span>Energia</span><strong>${money(data.energy)}</strong></div>
    <div><span>Taxa fixa</span><strong>${money(data.fixed)}</strong></div>
    <div class="total"><span>Total estimado</span><strong>${money(data.total)}</strong></div>
  `;
}

function renderAlerts() {
  const target = document.querySelector("#alertsList");
  if (!target) return;
  const alerts = [];
  topDevices().forEach((device) => {
    if (monthly(device) >= 80) alerts.push(["critical", "Crítico", `${device.name} está acima da média da casa.`]);
    else if (device.hours > 12) alerts.push(["warning", "Atenção", `${device.name} fica ligado mais de 12h por dia.`]);
  });
  if (state.devices.some((device) => device.category === "ILUMINACAO" && monthly(device) < 20)) {
    alerts.push(["good", "Bom", "A iluminação está com consumo controlado."]);
  }
  if (!alerts.length) alerts.push(["good", "Bom", "Não há alertas críticos neste momento."]);

  target.innerHTML = alerts.map(([type, title, text]) => `
    <article class="alert-card ${type}">
      <strong>${title}</strong>
      <span>${text}</span>
    </article>
  `).join("");
}

function renderSavings() {
  const range = document.querySelector("#savingHours");
  if (!range) return;
  const hours = Number(range.value);
  setText("#savingHoursLabel", `${hours}h`);

  const current = bill().total;
  const reducedConsumption = state.devices.reduce((sum, device) => {
    return sum + (device.power * Math.max(0, device.hours - hours) * 30) / 1000;
  }, 0);
  const saving = current - estimatedBill(reducedConsumption);
  document.querySelector("#savingSummary").innerHTML = `
    <div><span>Economia mensal</span><strong>${money(saving)}</strong></div>
    <div><span>Economia anual</span><strong>${money(saving * 12)}</strong></div>
  `;
  document.querySelector("#savingList").innerHTML = topDevices().slice(0, 5).map((device) => {
    const savedKwh = (device.power * Math.min(device.hours, hours) * 30) / 1000;
    const savedMt = savedKwh * tariff(totalConsumption()).price;
    return `<article class="saving-card"><span>${device.name}</span><strong>${money(savedMt)}/mês</strong></article>`;
  }).join("");
}

function estimatedBill(consumption) {
  const tier = tariff(consumption);
  return consumption * tier.price + EDM.fixed;
}

function renderReport() {
  const target = document.querySelector("#reportContent");
  if (!target) return;
  const data = bill();
  const top = topDevices()[0];
  const generatedAt = formatDateTime(new Date());
  const status = state.devices.length ? "Com dados registados" : "Sem aparelhos registados";
  target.innerHTML = `
    <section class="report-section">
      <h3>Dados do relatório</h3>
      <p><strong>Utilizador:</strong> ${state.user.name}</p>
      <p><strong>Residência:</strong> ${state.user.home}</p>
      <p><strong>Data e hora:</strong> ${generatedAt}</p>
      <p><strong>Estado:</strong> ${status}</p>
    </section>
    <section class="report-section">
      <h3>Resumo energético</h3>
      <p><strong>Aparelhos registados:</strong> ${state.devices.length}</p>
      <p><strong>Consumo mensal:</strong> ${data.consumption.toFixed(1)} kWh</p>
      <p><strong>Factura estimada:</strong> ${money(data.total)}</p>
      <p><strong>Maior consumidor:</strong> ${top ? top.name : "Sem dados"}</p>
    </section>
    <section class="report-section">
      <h3>Observação</h3>
      <p>${state.devices.length ? "Reduzir horas nos aparelhos de maior consumo tende a gerar a maior poupança." : "Registe aparelhos para gerar análise, alertas e recomendações de poupança."}</p>
    </section>
  `;
}

function formatDateTime(date) {
  return new Intl.DateTimeFormat("pt-MZ", {
    dateStyle: "medium",
    timeStyle: "short"
  }).format(date);
}

function reportLines() {
  const data = bill();
  const top = topDevices()[0];
  const generatedAt = formatDateTime(new Date());
  const lines = [
    "ENERMOZ - RELATORIO ENERGETICO",
    "",
    `Utilizador: ${state.user.name}`,
    `Residencia: ${state.user.home}`,
    `Data e hora: ${generatedAt}`,
    "",
    "RESUMO",
    `Aparelhos registados: ${state.devices.length}`,
    `Consumo mensal: ${data.consumption.toFixed(1)} kWh`,
    `Factura estimada: ${money(data.total)}`,
    `Maior consumidor: ${top ? top.name : "Sem dados"}`,
    "",
    "APARELHOS"
  ];

  if (!state.devices.length) {
    lines.push("Nenhum aparelho registado.");
  } else {
    topDevices().forEach((device, index) => {
      lines.push(`${index + 1}. ${device.name} - ${monthly(device).toFixed(1)} kWh/mes - ${device.location || "Sem localizacao"}`);
    });
  }

  lines.push("");
  lines.push("OBSERVACAO");
  lines.push(state.devices.length
    ? "Reduzir horas nos aparelhos de maior consumo tende a gerar a maior poupanca."
    : "Registe aparelhos para gerar analise, alertas e recomendacoes de poupanca.");

  return lines;
}

function downloadReportPdf() {
  const blob = createSimplePdf(reportLines());
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = `EnerMoz_Relatorio_${new Date().toISOString().slice(0, 10)}.pdf`;
  link.click();
  URL.revokeObjectURL(url);
}

function createSimplePdf(lines) {
  const safeLines = lines.map((line) => removeAccents(line).slice(0, 92));
  const content = [
    "BT",
    "/F1 18 Tf",
    "50 790 Td",
    `(${escapePdf(safeLines[0])}) Tj`,
    "/F1 11 Tf"
  ];

  safeLines.slice(1).forEach((line) => {
    content.push("0 -18 Td");
    content.push(`(${escapePdf(line)}) Tj`);
  });
  content.push("ET");

  const stream = content.join("\n");
  const objects = [
    "1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj",
    "2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj",
    "3 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >> endobj",
    "4 0 obj << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> endobj",
    `5 0 obj << /Length ${stream.length} >> stream\n${stream}\nendstream endobj`
  ];

  let pdf = "%PDF-1.4\n";
  const offsets = [0];
  objects.forEach((object) => {
    offsets.push(pdf.length);
    pdf += `${object}\n`;
  });
  const xref = pdf.length;
  pdf += `xref\n0 ${objects.length + 1}\n`;
  pdf += "0000000000 65535 f \n";
  offsets.slice(1).forEach((offset) => {
    pdf += `${String(offset).padStart(10, "0")} 00000 n \n`;
  });
  pdf += `trailer << /Size ${objects.length + 1} /Root 1 0 R >>\nstartxref\n${xref}\n%%EOF`;

  return new Blob([pdf], { type: "application/pdf" });
}

function escapePdf(text) {
  return text.replace(/\\/g, "\\\\").replace(/\(/g, "\\(").replace(/\)/g, "\\)");
}

function removeAccents(text) {
  return text.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
}

function renderLineChart(id) {
  const total = totalConsumption() / 30;
  const values = [0.8, 0.95, 1.05, 0.9, 1.12, 1.18, 1].map((factor) => Number((total * factor).toFixed(2)));
  if (typeof Chart === "undefined") {
    renderFallback(id.replace("Chart", "Fallback"), ["Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom"], values);
    return;
  }
  const canvas = document.querySelector(`#${id}`);
  if (!canvas) return;
  makeChart(id, "line", ["Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom"], values);
}

function renderConsumptionCharts() {
  renderLineChart("weeklyChart");
  const categoryMap = new Map();
  state.devices.forEach((device) => {
    categoryMap.set(device.categoryLabel, (categoryMap.get(device.categoryLabel) || 0) + monthly(device));
  });
  if (typeof Chart === "undefined") {
    renderFallback("categoryFallback", [...categoryMap.keys()], [...categoryMap.values()].map((value) => Number(value.toFixed(1))));
    return;
  }
  makeChart("categoryChart", "doughnut", [...categoryMap.keys()], [...categoryMap.values()].map((value) => Number(value.toFixed(1))));
}

function renderFallback(id, labels, values) {
  const target = document.querySelector(`#${id}`);
  if (!target) return;
  const max = Math.max(...values, 1);
  target.classList.add("active");
  target.innerHTML = labels.map((label, index) => `
    <div class="fallback-row">
      <span>${label}</span>
      <div class="fallback-bar"><span style="width:${(values[index] / max) * 100}%"></span></div>
      <strong>${values[index].toFixed(1)}</strong>
    </div>
  `).join("");
}

function makeChart(id, type, labels, values) {
  const canvas = document.querySelector(`#${id}`);
  if (!canvas) return;
  if (state.charts[id]) state.charts[id].destroy();
  state.charts[id] = new Chart(canvas, {
    type,
    data: {
      labels,
      datasets: [{
        label: "kWh",
        data: values,
        borderColor: "#3f6f55",
        backgroundColor: type === "doughnut" ? ["#3f6f55", "#7a8f68", "#6d8a9b", "#b18a4a", "#a15d57"] : "rgba(63, 111, 85, 0.12)",
        borderWidth: type === "doughnut" ? 0 : 2,
        tension: 0.25,
        fill: type === "line"
      }]
    },
    options: {
      responsive: true,
      plugins: { legend: { display: type === "doughnut", position: "bottom" } },
      scales: type === "doughnut" ? {} : { x: { grid: { display: false } }, y: { beginAtZero: true } }
    }
  });
}

function setText(selector, value) {
  const element = document.querySelector(selector);
  if (element) element.textContent = value;
}

function showAuthMessage(message, type) {
  const target = document.querySelector("#authMessage");
  if (!target) return;
  target.textContent = message;
  target.className = message ? `auth-message show ${type}` : "auth-message";
}

function startSession(account) {
  localStorage.setItem(STORAGE_SESSION, account.email);
  localStorage.setItem(STORAGE_USER, JSON.stringify(account));
  window.location.href = "home.html";
}

function setupEvents() {
  const signup = document.querySelector("#signupForm");
  if (signup) {
    signup.addEventListener("submit", (event) => {
      event.preventDefault();
      const email = normalizeEmail(document.querySelector("#signupEmail").value);
      const currentAccounts = loadAccounts();
      if (currentAccounts.some((account) => account.email === email)) {
        showAuthMessage("Já existe uma conta com este email. Use a opção Entrar.", "error");
        return;
      }

      const account = {
        name: document.querySelector("#signupName").value.trim(),
        email,
        home: document.querySelector("#signupHome").value.trim(),
        password: document.querySelector("#signupPassword").value,
        createdAt: new Date().toISOString()
      };
      currentAccounts.push(account);
      saveAccounts(currentAccounts);
      localStorage.setItem(devicesKey(email), JSON.stringify([]));
      showAuthMessage("Conta criada com sucesso.", "success");
      setTimeout(() => window.location.href = "entrar.html", 700);
    });
  }

  const signin = document.querySelector("#signinForm");
  if (signin) {
    signin.addEventListener("submit", (event) => {
      event.preventDefault();
      const email = normalizeEmail(document.querySelector("#signinEmail").value);
      const password = document.querySelector("#signinPassword").value;
      const account = loadAccounts().find((item) => item.email === email && item.password === password);
      if (!account) {
        showAuthMessage("Email ou palavra-passe incorrectos.", "error");
        return;
      }
      showAuthMessage("Acesso confirmado. entrando...", "success");
      setTimeout(() => startSession(account), 350);
    });
  }

  const recover = document.querySelector("#recoverForm");
  if (recover) {
    recover.addEventListener("submit", (event) => {
      event.preventDefault();
      const email = normalizeEmail(document.querySelector("#recoverEmail").value);
      const password = document.querySelector("#recoverPassword").value;
      const currentAccounts = loadAccounts();
      const account = currentAccounts.find((item) => item.email === email);
      if (!account) {
        showAuthMessage("Não encontramos uma conta com este email.", "error");
        return;
      }
      account.password = password;
      saveAccounts(currentAccounts);
      showAuthMessage("Senha actualizada.", "success");
      setTimeout(() => window.location.href = "entrar.html", 900);
    });
  }

  const form = document.querySelector("#deviceForm");
  if (form) {
    form.addEventListener("submit", (event) => {
      event.preventDefault();
      const category = document.querySelector("#category").value;
      const name = document.querySelector("#name").value.trim();
      const powerInput = Number(document.querySelector("#power").value);
      const device = {
        name,
        category,
        categoryLabel: categoryName(category),
        hours: Number(document.querySelector("#hours").value),
        power: powerInput > 0 ? powerInput : estimatePower(name, category),
        location: document.querySelector("#location").value.trim(),
        notes: document.querySelector("#notes").value.trim()
      };
      const index = document.querySelector("#deviceIndex").value;
      if (index === "") state.devices.push(device);
      else state.devices[Number(index)] = device;
      saveDevices();
      form.reset();
      document.querySelector("#deviceIndex").value = "";
      setText("#formTitle", "Adicionar aparelho");
      renderDevices();
      buildSidebar();
    });
  }

  const list = document.querySelector("#deviceList");
  if (list) {
    list.addEventListener("click", (event) => {
      const edit = event.target.dataset.edit;
      const remove = event.target.dataset.remove;
      if (edit !== undefined) {
        const device = state.devices[Number(edit)];
        document.querySelector("#deviceIndex").value = edit;
        setText("#formTitle", "Editar aparelho");
        document.querySelector("#name").value = device.name;
        document.querySelector("#category").value = device.category;
        document.querySelector("#hours").value = device.hours;
        document.querySelector("#power").value = device.power;
        document.querySelector("#location").value = device.location || "";
        document.querySelector("#notes").value = device.notes || "";
      }
      if (remove !== undefined) {
        state.devices.splice(Number(remove), 1);
        saveDevices();
        renderDevices();
        buildSidebar();
      }
    });
  }

  document.querySelector("#resetForm")?.addEventListener("click", () => {
    document.querySelector("#deviceForm").reset();
    document.querySelector("#deviceIndex").value = "";
    setText("#formTitle", "Adicionar aparelho");
  });

  document.querySelector("#clearAll")?.addEventListener("click", () => {
    state.devices = [];
    saveDevices();
    renderDevices();
    buildSidebar();
  });

  document.querySelector("#savingHours")?.addEventListener("input", renderSavings);
  document.querySelector("#copyReport")?.addEventListener("click", async (event) => {
    await navigator.clipboard?.writeText(document.querySelector("#reportContent").innerText);
    event.currentTarget.textContent = "Copiado";
    setTimeout(() => event.currentTarget.textContent = "Copiar", 1200);
  });

  document.querySelector("#downloadReport")?.addEventListener("click", downloadReportPdf);
}

function init() {
  if (authPages.includes(page) && state.user) {
    window.location.href = "home.html";
    return;
  }
  if (!authPages.includes(page) && !state.user) {
    window.location.href = "entrar.html";
    return;
  }
  if (!authPages.includes(page)) {
    buildSidebar();
    buildTopbar();
  }
  setupEvents();
  if (page === "home") renderHome();
  if (page === "devices") renderDevices();
  if (page === "consumption") renderConsumptionCharts();
  if (page === "bill") renderBill();
  if (page === "alerts") renderAlerts();
  if (page === "saving") renderSavings();
  if (page === "reports") renderReport();
}

init();
