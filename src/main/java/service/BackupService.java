package com.cms.cms.service;

import com.cms.cms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class BackupService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private FormSubmissionRepository formSubmissionRepository;

    @Autowired
    private FaqRepository faqRepository;

    @Autowired
    private TestimonialRepository testimonialRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SettingRepository settingRepository;

    public String generateBackup() {
        StringBuilder sql = new StringBuilder();
        sql.append("-- CMS Database Backup\n");
        sql.append("-- Generated on: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");
        sql.append("SET FOREIGN_KEY_CHECKS = 0;\n\n");

        // Export all tables using JdbcTemplate
        String[] tables = {"users", "categories", "tags", "articles", "comments", "menus", "menu_items",
                "products", "forms", "form_submissions", "faqs", "testimonials", "events", "settings"};

        for (String table : tables) {
            sql.append("-- Table: ").append(table).append("\n");
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM " + table);
            if (rows.isEmpty()) {
                sql.append("-- No data in ").append(table).append("\n\n");
                continue;
            }
            // Build INSERT statement
            String columns = String.join(", ", rows.get(0).keySet());
            sql.append("INSERT INTO ").append(table).append(" (").append(columns).append(") VALUES\n");
            for (int i = 0; i < rows.size(); i++) {
                Map<String, Object> row = rows.get(i);
                sql.append("(");
                int colIndex = 0;
                for (String key : row.keySet()) {
                    Object val = row.get(key);
                    if (val == null) {
                        sql.append("NULL");
                    } else if (val instanceof Number) {
                        sql.append(val);
                    } else if (val instanceof Boolean) {
                        sql.append((Boolean) val ? 1 : 0);
                    } else {
                        sql.append("'").append(escape(val.toString())).append("'");
                    }
                    if (colIndex < row.keySet().size() - 1) sql.append(", ");
                    colIndex++;
                }
                sql.append(")");
                if (i < rows.size() - 1) sql.append(",\n");
            }
            sql.append(";\n\n");
        }

        sql.append("SET FOREIGN_KEY_CHECKS = 1;\n");
        auditLogService.log("BACKUP", "Database", null, "Database backup generated");
        return sql.toString();
    }

    @Transactional
    public String restoreBackup(String sqlScript) {
        String[] statements = sqlScript.split(";");
        int executed = 0;
        int errors = 0;
        StringBuilder errorMessages = new StringBuilder();

        for (String stmt : statements) {
            String trimmed = stmt.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("--")) continue;
            // Remove trailing semicolon if any (already split)
            try {
                jdbcTemplate.execute(trimmed);
                executed++;
            } catch (Exception e) {
                errors++;
                errorMessages.append("Error in: ").append(trimmed.substring(0, Math.min(100, trimmed.length())))
                        .append("... -> ").append(e.getMessage()).append("\n");
            }
        }

        String result = String.format("Restore completed. Executed: %d statements, Errors: %d", executed, errors);
        if (errors > 0) {
            result += "\n" + errorMessages.toString();
            auditLogService.log("RESTORE", "Database", null, "Database restore completed with errors: " + errors);
        } else {
            auditLogService.log("RESTORE", "Database", null, "Database restore completed successfully");
        }
        return result;
    }

    private String escape(String input) {
        if (input == null) return "";
        return input.replace("'", "''");
    }
}