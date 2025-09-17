const API_BASE = "http://localhost:9090/api"; // change if backend runs on another port

function showSection(id) {
  document.querySelectorAll("section").forEach(sec => sec.classList.add("hidden"));
  document.getElementById(id).classList.remove("hidden");
}

// ---------- Customers ----------
const customerForm = document.getElementById("customerForm");
customerForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const data = {
    name: document.getElementById("custName").value,
    email: document.getElementById("custEmail").value,
    phone: document.getElementById("custPhone").value
  };
  await fetch(`${API_BASE}/customers`, {
    method: "POST", headers: {"Content-Type": "application/json"}, body: JSON.stringify(data)
  });
  loadCustomers();
});

async function loadCustomers() {
  const res = await fetch(`${API_BASE}/customers`);
  const customers = await res.json();
  const table = document.getElementById("customerTable");
  table.innerHTML = "";
  customers.forEach(c => {
    table.innerHTML += `<tr><td>${c.id}</td><td>${c.name}</td><td>${c.email}</td><td>${c.phone}</td></tr>`;
  });
}

// ---------- Accounts ----------
const accountForm = document.getElementById("accountForm");

accountForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  const data = {
    customerId: document.getElementById("accCustomerId").value,
    accountNumber: document.getElementById("accNumber").value,
    type: document.getElementById("accType").value,
    initialDeposit: document.getElementById("accDeposit").value
  };

  await fetch(`${API_BASE}/accounts`, {
    method: "POST",
    headers: {"Content-Type": "application/json"},
    body: JSON.stringify(data)
  });

  loadAccounts();
});

async function loadAccounts() {
  const res = await fetch(`${API_BASE}/accounts`);
  const accounts = await res.json();

  const table = document.getElementById("accountTable");
  table.innerHTML = "";

  accounts.forEach(a => {
    table.innerHTML += `
      <tr>
        <td>${a.id}</td>
        <td>${a.accountNumber}</td>
        <td>${a.type}</td>
        <td>${a.balance}</td>
        <td>${a.customer.name}</td>
      </tr>`;
  });
}


// Deposit / Withdraw
const transactionForm = document.getElementById("transactionForm");
transactionForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  const id = document.getElementById("txnAccountId").value.trim();
  const amount = document.getElementById("txnAmount").value.trim();
  const type = document.getElementById("txnType").value.trim(); // must be "deposit" or "withdraw"
  const desc = document.getElementById("txnDesc").value.trim();

  if (!["deposit", "withdraw"].includes(type)) {
    alert("Invalid transaction type. Choose 'deposit' or 'withdraw'.");
    return;
  }

  const response = await fetch(`${API_BASE}/accounts/${id}/${type}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ amount, description: desc })
  });

  if (response.ok) {
    loadAccounts();
    transactionForm.reset();
  } else {
    const error = await response.text();
    alert("Transaction failed: " + error);
  }
});


// Transfer
const transferForm = document.getElementById("transferForm");
transferForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  const data = {
    fromAccountId: document.getElementById("fromAcc").value.trim(),
    toAccountId: document.getElementById("toAcc").value.trim(),
    amount: document.getElementById("transferAmt").value.trim(),
    description: document.getElementById("transferDesc").value.trim()
  };

  try {
    const response = await fetch(`${API_BASE}/accounts/transfer`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data)
    });

    if (response.ok) {
      loadAccounts();
      transferForm.reset();
      alert("Transfer successful ✅");
    } else {
      const error = await response.text();
      alert("Transfer failed ❌: " + error);
    }
  } catch (err) {
    alert("Error while transferring: " + err.message);
  }
});


// ---------- Transactions ----------
async function loadTransactions() {
  const accId = document.getElementById("txnAccId").value.trim();
  if (!accId) {
    alert("Please enter/select an Account ID");
    return;
  }

  try {
    const res = await fetch(`${API_BASE}/transactions?accountId=${accId}`);
    if (!res.ok) {
      throw new Error(`Failed to load transactions: ${res.status}`);
    }

    const txns = await res.json();
    const table = document.getElementById("transactionTable");
    table.innerHTML = "";

    if (txns.length === 0) {
      table.innerHTML = `<tr><td colspan="5">No transactions found</td></tr>`;
      return;
    }

    txns.forEach(t => {
      table.innerHTML += `
        <tr>
          <td>${t.id}</td>
          <td>${t.type}</td>
          <td>${t.amount}</td>
          <td>${new Date(t.timestamp).toLocaleString()}</td>
          <td>${t.description}</td>
        </tr>`;
    });
  } catch (err) {
    alert(err.message);
  }
}


// Auto load customers and accounts at start
loadCustomers();
loadAccounts();
