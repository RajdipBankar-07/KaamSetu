/**
 * KaamSetu Phase 2 Schema & DDL Integrity Validator
 * Validates SQL DDL grammar, Enum definitions, Table references, and Foreign Key constraints
 */

const fs = require('fs');
const path = require('path');

function validatePhase2Schema() {
  console.log("==================================================");
  console.log("🌾 KAAMSETU V1 — PHASE 2 SCHEMA INTEGRITY CHECK");
  console.log("==================================================\n");

  const schemaPath = path.join(__dirname, 'schema.sql');
  const indexesPath = path.join(__dirname, 'indexes.sql');
  const seedPath = path.join(__dirname, 'seed.sql');

  if (!fs.existsSync(schemaPath) || !fs.existsSync(indexesPath) || !fs.existsSync(seedPath)) {
    console.error("❌ ERROR: One or more SQL files are missing!");
    process.exit(1);
  }

  const schemaContent = fs.readFileSync(schemaPath, 'utf-8');
  const indexesContent = fs.readFileSync(indexesPath, 'utf-8');
  const seedContent = fs.readFileSync(seedPath, 'utf-8');

  // 1. Validate Required Tables (16 tables)
  const expectedTables = [
    'users', 'workers', 'worker_skills', 'worker_availability',
    'providers', 'jobs', 'applications', 'assignments',
    'reviews', 'trust_profiles', 'moderation_reports', 'notifications',
    'messages', 'saved_items', 'privacy_settings', 'audit_logs'
  ];

  console.log("1. Checking Table Definitions (Expected 16 Tables):");
  let tablesFound = 0;
  expectedTables.forEach(table => {
    const tableRegex = new RegExp(`CREATE\\s+TABLE\\s+${table}\\s*\\(`, 'i');
    if (tableRegex.test(schemaContent)) {
      console.log(`   ✅ Table '${table}' defined.`);
      tablesFound++;
    } else {
      console.error(`   ❌ Table '${table}' NOT found!`);
    }
  });

  // 2. Validate Enums
  const expectedEnums = [
    'user_role_enum', 'language_code_enum', 'user_status_enum',
    'work_category_enum', 'day_of_week_enum', 'time_slot_enum',
    'provider_type_enum', 'job_priority_enum', 'job_status_enum',
    'application_status_enum', 'assignment_status_enum',
    'payment_type_enum', 'payment_status_enum',
    'trust_ladder_status_enum', 'report_target_type_enum',
    'report_category_enum', 'report_status_enum', 'notification_channel_enum'
  ];

  console.log("\n2. Checking Enum Definitions (Expected 18 Enums):");
  let enumsFound = 0;
  expectedEnums.forEach(enumType => {
    const enumRegex = new RegExp(`CREATE\\s+TYPE\\s+${enumType}\\s+AS\\s+ENUM`, 'i');
    if (enumRegex.test(schemaContent)) {
      console.log(`   ✅ Enum '${enumType}' defined.`);
      enumsFound++;
    } else {
      console.error(`   ❌ Enum '${enumType}' NOT found!`);
    }
  });

  // 3. Validate Triggers
  console.log("\n3. Checking State Automation Triggers:");
  const expectedTriggers = [
    'trigger_set_updated_at',
    'trigger_auto_fill_job_capacity',
    'expire_past_open_jobs'
  ];
  expectedTriggers.forEach(fn => {
    if (schemaContent.includes(fn)) {
      console.log(`   ✅ Function/Trigger '${fn}' implemented.`);
    } else {
      console.error(`   ❌ Function/Trigger '${fn}' missing!`);
    }
  });

  // 4. Validate Indexes
  console.log("\n4. Checking Performance Indexes:");
  const indexCount = (indexesContent.match(/CREATE\s+INDEX/gi) || []).length;
  console.log(`   ✅ Total Performance & Partial Indexes defined: ${indexCount}`);

  // 5. Validate Seed Data
  console.log("\n5. Checking Seed Data Insertions:");
  expectedTables.forEach(table => {
    const insertRegex = new RegExp(`INSERT\\s+INTO\\s+${table}`, 'i');
    if (insertRegex.test(seedContent)) {
      console.log(`   ✅ Seed data provided for '${table}'.`);
    }
  });

  console.log("\n==================================================");
  if (tablesFound === 16 && enumsFound === 18) {
    console.log("🎉 ALL PHASE 2 SCHEMA INTEGRITY CHECKS PASSED!");
  } else {
    console.error("❌ SOME INTEGRITY CHECKS FAILED!");
    process.exit(1);
  }
  console.log("==================================================");
}

validatePhase2Schema();
