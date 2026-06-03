import http from "k6/http";
import { check, sleep, group } from "k6";
import { textSummary, TextSummary } from "https://jslib.k6.io/k6-summary/0.0.1/index.js";

const BASE_URL = "http://localhost:32108";

// Load CSV feeder data (same data as gatling-tests/src/gatling/resources/numbers.csv)
// k6's open() resolves paths relative to the script's directory
const csvData = open("../data/numbers.csv", "utf8");
const rows = csvData.trim().split("\n").slice(1); // skip header
let rowIndex = 0;

function getNextPair() {
  const row = rows[rowIndex % rows.length];
  rowIndex++;
  const [num1, num2] = row.split(",");
  return { num1: num1.trim(), num2: num2.trim() };
}

// ── Shared check helper ──
function checkMathResponse(res, expectedOp) {
  return check(res, {
    [`${expectedOp}: status is 200`]: (r) => r.status === 200,
    [`${expectedOp}: has result field`]: (r) => {
      const body = JSON.parse(r.body);
      return body.result !== undefined;
    },
  });
}

// ── Scenario 1: Add Numbers Test ──
export function addNumbers() {
  group("add", function () {
    const res = http.get(`${BASE_URL}/add?num1=10&num2=20`, {
      headers: { Accept: "application/json" },
    });
    const body = JSON.parse(res.body);
    check(res, {
      "add: status is 200": (r) => r.status === 200,
      "add: result is 30": (r) => body.result === 30.0,
    });
  });
}

// ── Scenario 2: Multiply Numbers Test ──
export function multiplyNumbers() {
  group("multiply", function () {
    const res = http.get(`${BASE_URL}/multiply?num1=5&num2=6`, {
      headers: { Accept: "application/json" },
    });
    const body = JSON.parse(res.body);
    check(res, {
      "multiply: status is 200": (r) => r.status === 200,
      "multiply: result is 30": (r) => body.result === 30.0,
    });
  });
}

// ── Scenario 3: Mixed Operations Test (CSV-fed) ──
export function mixedOperations() {
  const pair = getNextPair();

  group("add random", function () {
    const res = http.get(
      `${BASE_URL}/add?num1=${pair.num1}&num2=${pair.num2}`,
      { headers: { Accept: "application/json" } }
    );
    checkMathResponse(res, "add");
  });

  sleep(1);

  group("multiply random", function () {
    const res = http.get(
      `${BASE_URL}/multiply?num1=${pair.num1}&num2=${pair.num2}`,
      { headers: { Accept: "application/json" } }
    );
    checkMathResponse(res, "multiply");
  });
}

// ── Load profile (mirrors Gatling MathApiSimulation) ──
//
// Gatling load profile:
//   addScenario:    ramp 1→10 vUs/s over 30s, hold 10 vUs/s for 60s
//   multiplyScenario: ramp 1→10 vUs/s over 30s, hold 10 vUs/s for 60s
//   mixedScenario:  constant 5 vUs/s for 90s
//
// k6 stages (executed sequentially):
//   stage 1: ramp 1→10 vUs over 30s  (add + multiply)
//   stage 2: hold 10 vUs for 60s     (add + multiply)
//   stage 3: ramp 1→5 vUs over 15s   (mixed)
//   stage 4: hold 5 vUs for 90s      (mixed)

// ── Main entry point ──
export default function () {
  addNumbers();
  multiplyNumbers();
  mixedOperations();
}

export const options = {
  stages: [
    { duration: "30s", target: 10 },  // ramp up add + multiply
    { duration: "60s", target: 10 },  // steady add + multiply
    { duration: "15s", target: 5 },   // ramp up mixed
    { duration: "90s", target: 5 },   // steady mixed
  ],
  thresholds: {
    http_req_duration: ["p(95)<500"],  // 95th percentile < 500ms
    http_req_failed: ["rate<0.05"],    // < 5% failure rate
  },
  summaryTrendStats: ["min", "max", "med", "avg", "p(90)", "p(95)", "p(99)", "count"],
};

export function handleSummary(data) {
  return {
    "stdout": textSummary(data, { indent: " ", enableColors: true }),
    "reports/k6-summary.txt": textSummary(data, { indent: " ", enableColors: false }),
  };
}
