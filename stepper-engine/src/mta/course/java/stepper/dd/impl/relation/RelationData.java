package mta.course.java.stepper.dd.impl.relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelationData {

    private List<String> columns;
    private List<SingleRow> rows;

    public RelationData(List<String> columns) {
        this.columns = columns;
        rows = new ArrayList<>();
    }

    public List<String> getRowDataByColumnsOrder(int rowId) {
        return new ArrayList<>();
    }
    public List<String> getColumns() {
        return new ArrayList<>(columns);
    }
    public List<SingleRow> getRows() {
        return new ArrayList<>(rows);
    }
    public int getNumberOfRows() {return rows.size();}

    public String getRowData(int rowIndex, String columnName) {
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            throw new IndexOutOfBoundsException("Invalid row index: " + rowIndex);
        }

        SingleRow row = rows.get(rowIndex);
        if (!row.containsKey(columnName)) {
            throw new IllegalArgumentException("Unknown column: " + columnName);
        }

        return row.get(columnName);
    }
    public List<Map<String, String>> getData() {
        List<Map<String, String>> data = new ArrayList<>();
        for (SingleRow row : rows) {
            data.add(row.getData());
        }
        return data;
    }
    public void addRowToTable(String column1Value, String column2Value, String column3Value) {
        SingleRow newRow = new SingleRow();
        newRow.addData(columns.get(0), column1Value);
        newRow.addData(columns.get(1), column2Value);
        newRow.addData(columns.get(2), column3Value);
        rows.add(newRow);
    }
    public SingleRow getRow(int index) {
        if (index < 0 || index >= rows.size()) {
            throw new IndexOutOfBoundsException("Invalid row index: " + index);
        }
        return rows.get(index);
    }

    public static class SingleRow {
        private Map<String, String> data;

        public SingleRow() {
            data = new HashMap<>();
        }

        public void addData(String columnName, String value) {
            data.put(columnName, value);
        }
        public boolean containsKey(String columnName) {
            return data.containsKey(columnName);
        }

        public String get(String columnName) {
            return data.get(columnName);
        }

        public Map<String, String> getData() {
            return new HashMap<>(data);
        }
    }
    @Override
    public String toString() {
        Map<String, Integer> maxWidths = new HashMap<>();

        // Calculate maximum width for each column
        for (String col : columns) {
            int maxWidth = col.length();
            for (SingleRow row : rows) {
                String value = row.data.getOrDefault(col, "");
                maxWidth = Math.max(maxWidth, value.length());
            }
            maxWidths.put(col, maxWidth+1);
        }

        StringBuilder sb = new StringBuilder();

        // Add header row
        for (String col : columns) {
            int width = maxWidths.get(col);
            sb.append(String.format("%-" + width + "s", col));
        }
        sb.append("\n");

        // Add separator row
        for (String col : columns) {
            int width = maxWidths.get(col);
            sb.append(String.format("%-" + width + "s", "").replace(' ', '-'));
        }
        sb.append("\n");

        // Add data rows
        for (SingleRow row : rows) {
            for (String col : columns) {
                int width = maxWidths.get(col);
                String value = row.data.getOrDefault(col, "");
                sb.append(String.format("%-" + width + "s", value));
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}