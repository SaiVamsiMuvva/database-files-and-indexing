import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

public class DBSqlLite {
	// This can be changed to whatever you like
	static String prompt = "vamsisql> ";

	
	static String inUseSchema = "information_schema";

	static String schemataTable = "information_schema.schemata.tbl";
	static String columnsTable = "information_schema.columns.tbl";
	static String tablesTable = "information_schema.table.tbl";

	static RandomAccessFile schemataTableFile;

	

	public static void main(String[] args) {
		splashScreen();

		//makeInfromationSchema();

		/*
		 * The Scanner class is used to collect user commands from the prompt
		 * There are many ways to do this. This is just one.
		 *
		 * Each time the semicolon (;) delimiter is entered, the userCommand
		 * String is re-populated.
		 */
		Scanner scanner = new Scanner(System.in).useDelimiter(";");
		String userCommand; // Variable to collect user input from the prompt

		do { // do-while !exit
			System.out.print(prompt);
			userCommand = scanner.next().trim();

			/*
			 * This switch handles a very small list of commands of known
			 * syntax. You will probably want to write a parse(userCommand)
			 * method to to interpret more complex commands.
			 */
			switch (userCommand) {
			
			case "show schemas":
				try {
					showSchemas();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case "create schema":
				createSchema(scanner.next().trim());
				break;
			case "use":
				useSchema(scanner.next().trim());
				break;
			case "show tables":
				try {
					showTables();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case "create table":
				Scanner scanner1 = new Scanner(System.in).useDelimiter("\\{");
				String table = scanner1.next().trim();
				try {
					createTable(table);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case "insert into table":
				Scanner scanner2 = new Scanner(System.in).useDelimiter(" " + "VALUES" + " ");
				String tableName = scanner2.next().trim();
				try {
					insertIntoTable(tableName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case "select * from":
				Scanner scanner3 = new Scanner(System.in).useDelimiter(" WHERE ");
				String tableName1 = scanner3.next().trim();
				Scanner scanner4 = new Scanner(System.in).useDelimiter(" = ");
				String colName = scanner4.next().trim();
				String value = scanner.next().trim();
				 selectRows(tableName1,colName,value);
				break;
			case "help":
				help();
				break;
			case "version":
				version();
				break;
			default:
				System.out.println("I didn't understand the command: \"" + userCommand + "\"");
			}
		} while (!userCommand.equalsIgnoreCase("exit"));
		System.out.println("Exiting...");

	} /* End main() method */

	// ===========================================================================
	// STATIC METHOD DEFINTIONS BEGIN HERE
	// ===========================================================================

	/**
	 * Display the welcome "splash screen"
	 */
	public static void splashScreen() {
		System.out.println(line("*", 80));
		System.out.println("Welcome to DBSqlLite"); // Display the string.
		version();
		System.out.println("Type \"help;\" to display supported commands or read READ_ME.txt to understand the commands");
		System.out.println(line("*", 80));
	}

	/**
	 * Help: Display supported commands
	 */
	public static void help() {
		System.out.println(line("*", 80));
		System.out.println();
		System.out.println("\tshow schemas;   Display all schemas.");
		System.out.println("\tcreate schema;  Create a new schema.");
		System.out.println("\tuse schema;   Use a Schema");
		System.out.println("\tshow tables;   Show all tables in the current schema");
		System.out.println("\tcreate table;   Create a new table in the current schema");
		System.out.println("\tinsert into table;   Display all schemas.");
		System.out.println("\tselect * from;     Display records");
		System.out.println("\tversion;       Show the program version.");
		System.out.println("\thelp;          Show this help information");
		System.out.println("\texit;          Exit the program");
		System.out.println();
		System.out.println();
		System.out.println(line("*", 80));
	}

	/**
	 * @param s
	 *            The String to be repeated
	 * @param num
	 *            The number of time to repeat String s.
	 * @return String A String object, which is the String s appended to itself
	 *         num times.
	 */
	public static String line(String s, int num) {
		String a = "";
		for (int i = 0; i < num; i++) {
			a += s;
		}
		return a;
	}

	/**
	 * @param num
	 *            The number of newlines to be displayed to <b>stdout</b>
	 */
	
	public static void newline(int num) {
		for (int i = 0; i < num; i++) {
			System.out.println();
		}
	}

	public static void version() {
		System.out.println("DBSqlLite v1.0\n");
	}

	/**
	 * Display all the schemas including the Information Schema"
	 */
	
	private static void showSchemas() throws IOException {

		try {
			RandomAccessFile schemataTableFile = new RandomAccessFile(schemataTable, "rw");
			while (true) {
				try {
					byte varcharLength = schemataTableFile.readByte();
					for (int i = 0; i < varcharLength; i++) {
						System.out.print((char) schemataTableFile.readByte());
					}
					System.out.println();
				} catch (EOFException e) {
					break;
				}
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		System.out.println();
	}
	
	/**
	 * Create a new Schema
	 */

	private static void createSchema(String schemaName) {
		try {
			schemataTableFile = new RandomAccessFile(schemataTable, "rw");
			schemataTableFile.seek(schemataTableFile.length());
			schemataTableFile.writeByte(schemaName.length());
			schemataTableFile.writeBytes(schemaName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Use a schema
	 */

	private static void useSchema(String schemaName) {
		inUseSchema = schemaName;
	}
	
	/**
	 * Show tables from the selected schema
	 */

	private static void showTables() throws IOException {
		try {
			RandomAccessFile tablesTableFile = new RandomAccessFile(tablesTable, "rw");
			int index = 0;
			while (true) {
				try {
					String schema = "";
					tablesTableFile.seek(index);
					byte varcharLength = tablesTableFile.readByte();
					for (int i = 0; i < varcharLength; i++) {
						schema = schema + String.valueOf((char) tablesTableFile.readByte());
					}
					if (schema.equals(inUseSchema)) {
						byte length = tablesTableFile.readByte();
						for (int i = 0; i < length; i++) {
							System.out.print((char) tablesTableFile.readByte());
						}
						System.out.println();
						index = index + varcharLength + length + 2 + 8;
					} else {
						byte length = tablesTableFile.readByte();
						index = index + varcharLength + length + 2 + 8;
					}
				} catch (EOFException e) {
					break;
				}
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Create a new table in the selected schema
	 */

	private static void createTable(String table) throws IOException {
		RandomAccessFile tablesTableFile = new RandomAccessFile(tablesTable, "rw");
		RandomAccessFile columnsTableFile = new RandomAccessFile(columnsTable, "rw");
		RandomAccessFile newTableFile = new RandomAccessFile(inUseSchema + "." + table + ".tbl", "rw");
		tablesTableFile.seek(tablesTableFile.length());
		columnsTableFile.seek(columnsTableFile.length());
		tablesTableFile.writeByte(inUseSchema.length());
		tablesTableFile.writeBytes(inUseSchema);
		tablesTableFile.writeByte(table.length());
		tablesTableFile.writeBytes(table);
		tablesTableFile.writeLong(0);

		Scanner scanner = new Scanner(System.in).useDelimiter("\\}" + ";");
		String tabDef = scanner.next().trim();
		String[] colDef = tabDef.split(",");
		int ordinal = 0;
		for (String str : colDef) {
			ordinal++;
			String[] colDetails = str.split("\\s+");
			if (colDetails.length < 2 || colDetails.length == 3 || colDetails.length > 4) {
				System.out.println("Syntax error");
				return;
			}
			if (colDetails.length >= 2) {
				String col1 = colDetails[0];
				RandomAccessFile newIndexFile = new RandomAccessFile(inUseSchema + "." + table + "." + col1 + ".ndx",
						"rw");
				String col2 = colDetails[1];
				columnsTableFile.writeByte(inUseSchema.length());
				columnsTableFile.writeBytes(inUseSchema);
				columnsTableFile.writeByte(table.length());
				columnsTableFile.writeBytes(table);
				columnsTableFile.writeByte(col1.length());
				columnsTableFile.writeBytes(col1);
				columnsTableFile.writeInt(ordinal);
				columnsTableFile.writeByte(col2.length());
				columnsTableFile.writeBytes(col2);
			}
			
			if (colDetails.length == 2) {
				columnsTableFile.writeByte("YES".length()); // COLUMN_KEY
				columnsTableFile.writeBytes("YES");

				columnsTableFile.writeByte("".length()); // COLUMN_KEY
				columnsTableFile.writeBytes("");
			}
			if (colDetails.length == 4) {
				String col3 = colDetails[2] + " " + colDetails[3];
				if (col3.equals("PRIMARY KEY")) {
					columnsTableFile.writeByte("NO".length()); // COLUMN_KEY
					columnsTableFile.writeBytes("NO");

					columnsTableFile.writeByte("PRI".length()); // COLUMN_KEY
					columnsTableFile.writeBytes("PRI");
				} else if (col3.equals("NOT NULL")) {
					columnsTableFile.writeByte("NO".length()); // COLUMN_KEY
					columnsTableFile.writeBytes("NO");

					columnsTableFile.writeByte("".length()); // COLUMN_KEY
					columnsTableFile.writeBytes("");
				} else {
					System.out.println("Syntax Error");
					return;
				}
			}
		}
	}

	/**
	 * Insert a row into a table
	 */
	
	private static void insertIntoTable(String table) throws IOException {
		Scanner scanner = new Scanner(System.in).useDelimiter(";");
		String valDef = scanner.next().trim();
		String valuesDef = StringUtils.substringBetween(valDef, "(", ")");
		String[] values = valuesDef.split(",");
		RandomAccessFile newTableFile = new RandomAccessFile(inUseSchema + "." + table + ".tbl", "rw");
		long writePos = newTableFile.length();
		
		for(int i = 0; i < values.length; i++){
			String type = getColumnType(table, i + 1);
			String colname = getColumnName(table, i + 1);
			String[] props = getColumnProperties(table, colname);
			if(props[1].equalsIgnoreCase("pri")){
				if(isPrimaryViolated(table, values[i], type , colname)){
					System.out.println(colname + " Primary key constraint violated");
					return;
				}
			}
		}
		
		for (int i = 0; i < values.length; i++) {
			String type = getColumnType(table, i + 1);

			String colname = getColumnName(table, i + 1);
			
			writeToDataAndIndex(table, values[i], type, colname , writePos);
		}
		updateRowCountInTables(table);
	}

	/**
	 * Get the data type of a column, given table name and ordinal position
	 */
	
	private static String getColumnType(String inUseTable, int position) throws IOException {
		try {
			RandomAccessFile columnsTableFile = new RandomAccessFile(columnsTable, "rw");
			int index = 0;
			while (true) {
				try {
					columnsTableFile.seek(index);
					String schema = "";
					byte varcharLength = columnsTableFile.readByte();
					for (int i = 0; i < varcharLength; i++) {
						schema = schema + String.valueOf((char) columnsTableFile.readByte());
					}

					if (schema.equals(inUseSchema)) {
						byte length = columnsTableFile.readByte();
						String table = "";
						for (int i = 0; i < length; i++) {
							table = table + String.valueOf((char) columnsTableFile.readByte());
						}

						if (table.equals(inUseTable)) {
							byte length1 = columnsTableFile.readByte();
							String column = "";
							for (int i = 0; i < length1; i++) {
								column = column + ((char) columnsTableFile.readByte());
							}

							int ord_pos = columnsTableFile.readInt();
							if (ord_pos == position) {
								byte length2 = columnsTableFile.readByte();
								String type = "";
								for (int i = 0; i < length2; i++) {
									type = type + ((char) columnsTableFile.readByte());
								}
								index = (int) columnsTableFile.getFilePointer();
								byte length4 = columnsTableFile.readByte();
								index += 1 + length4;
								columnsTableFile.seek(index);
								byte len = columnsTableFile.readByte();
								if (len == 0) {
									index += 1;
								} else {
									index += 1 + len;
								}
								return type;
							} else {
								index = (int) (columnsTableFile.getFilePointer() + 1 + columnsTableFile.readByte());
								columnsTableFile.seek(index);
								index += 1 + columnsTableFile.readByte();
								columnsTableFile.seek(index);
								byte len = columnsTableFile.readByte();
								if (len == 0) {
									index += 1;
								} else {
									index += 1 + len;
								}
								continue;
							}

						} else {
							index += 1 + varcharLength + 1 + length + 1 + columnsTableFile.readByte();
							columnsTableFile.seek(index);
							index += 4 + columnsTableFile.readByte();
							columnsTableFile.seek(index);
							index += 1 + columnsTableFile.readByte();
							columnsTableFile.seek(index);
							index += 2 + columnsTableFile.readByte();
							continue;
						}
					} else {
						index += 1 + varcharLength + 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 4 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						byte len = columnsTableFile.readByte();

						if (len == 0) {
							index += 1;
						} else {
							index += 1 + len;
						}
					}
				} catch (EOFException e) {
					break;
				}
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return "";
	}

	/**
	 * Get the data type of a column, given table name and column name
	 */
	
	private static String getColumnType(String inUseTable, String col) throws IOException {
		try {
			RandomAccessFile columnsTableFile = new RandomAccessFile(columnsTable, "rw");
			int index = 0;

			while (true) {
				String type = "";
				try {
					columnsTableFile.seek(index);
					String schema = "";
					byte varcharLength = columnsTableFile.readByte();
					for (int i = 0; i < varcharLength; i++) {
						schema = schema + String.valueOf((char) columnsTableFile.readByte());
					}

					if (schema.equals(inUseSchema)) {
						byte length = columnsTableFile.readByte();
						String table = "";
						for (int i = 0; i < length; i++) {
							table = table + String.valueOf((char) columnsTableFile.readByte());
						}

						if (table.equals(inUseTable)) {
							byte length1 = columnsTableFile.readByte();
							String column = "";
							for (int i = 0; i < length1; i++) {
								column = column + ((char) columnsTableFile.readByte());
							}

							columnsTableFile.readInt();
							byte length2 = columnsTableFile.readByte();

							for (int i = 0; i < length2; i++) {
								type = type + ((char) columnsTableFile.readByte());
							}
							index = (int) columnsTableFile.getFilePointer();
							byte length4 = columnsTableFile.readByte();
							index += 1 + length4;
							columnsTableFile.seek(index);
							byte len = columnsTableFile.readByte();
							if (len == 0) {
								index += 1;
							} else {
								index += 1 + len;
							}
							if (column.equals(col)) {
								return type;
							} else {
								continue;
							}

						} else {
							index += 1 + varcharLength + 1 + length + 1 + columnsTableFile.readByte();
							columnsTableFile.seek(index);
							index += 4 + columnsTableFile.readByte();
							columnsTableFile.seek(index);
							index += 1 + columnsTableFile.readByte();
							columnsTableFile.seek(index);
							index += 2 + columnsTableFile.readByte();
							continue;
						}
					} else {
						index += 1 + varcharLength + 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 4 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						byte len = columnsTableFile.readByte();

						if (len == 0) {
							index += 1;
						} else {
							index += 1 + len;
						}
					}
				} catch (EOFException e) {
					break;
				}
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return "";
	}
	
	/**
	 * Get the data types of all columns in a table
	 */
	
	private static ArrayList<String> getColumnType(String inUseTable) throws IOException {
		try {
			ArrayList<String> lis = new ArrayList<>();
			
			RandomAccessFile columnsTableFile = new RandomAccessFile(columnsTable, "rw");
			int index = 0;

			while (true) {
				String type = "";
				try {
					columnsTableFile.seek(index);
					String schema = "";
					byte varcharLength = columnsTableFile.readByte();
					for (int i = 0; i < varcharLength; i++) {
						schema = schema + String.valueOf((char) columnsTableFile.readByte());
					}

					if (schema.equals(inUseSchema)) {
						byte length = columnsTableFile.readByte();
						String table = "";
						for (int i = 0; i < length; i++) {
							table = table + String.valueOf((char) columnsTableFile.readByte());
						}

						if (table.equals(inUseTable)) {
							byte length1 = columnsTableFile.readByte();
							String column = "";
							for (int i = 0; i < length1; i++) {
								column = column + ((char) columnsTableFile.readByte());
							}
							columnsTableFile.readInt();
							byte length2 = columnsTableFile.readByte();

							for (int i = 0; i < length2; i++) {
								type = type + ((char) columnsTableFile.readByte());
							}
							index = (int) columnsTableFile.getFilePointer();
							byte length4 = columnsTableFile.readByte();
							index += 1 + length4;
							columnsTableFile.seek(index);
							byte len = columnsTableFile.readByte();
							if (len == 0) {
								index += 1;
							} else {
								index += 1 + len;
							}							
							lis.add(type);

						} else {
							index += 1 + varcharLength + 1 + length + 1 + columnsTableFile.readByte();
							columnsTableFile.seek(index);
							index += 4 + columnsTableFile.readByte();
							columnsTableFile.seek(index);
							index += 1 + columnsTableFile.readByte();
							columnsTableFile.seek(index);
							index += 2 + columnsTableFile.readByte();
							continue;
						}
					} else {
						index += 1 + varcharLength + 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 4 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						byte len = columnsTableFile.readByte();

						if (len == 0) {
							index += 1;
						} else {
							index += 1 + len;
						}
					}
				} catch (EOFException e) {
					return lis;
				}
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * Get the name of a column using the table name and its ordinal position, which can be later used to get its data type/properties
	 */
	
	private static String getColumnName(String inUseTable, int position) throws IOException {

		try {
			RandomAccessFile columnsTableFile = new RandomAccessFile(columnsTable, "rw");
			int index = 0;
			while (true) {
				try {
					columnsTableFile.seek(index);
					String schema = "";
					byte varcharLength = columnsTableFile.readByte();
					for (int i = 0; i < varcharLength; i++) {
						schema = schema + String.valueOf((char) columnsTableFile.readByte());
					}

					if (schema.equals(inUseSchema)) {
						byte length = columnsTableFile.readByte();
						String table = "";
						for (int i = 0; i < length; i++) {
							table = table + String.valueOf((char) columnsTableFile.readByte());
						}

						if (table.equals(inUseTable)) {
							byte length1 = columnsTableFile.readByte();
							String column = "";
							for (int i = 0; i < length1; i++) {
								column = column + ((char) columnsTableFile.readByte());
							}

							int ord_pos = columnsTableFile.readInt();
							if (ord_pos == position) {
								byte length2 = columnsTableFile.readByte();
								String type = "";
								for (int i = 0; i < length2; i++) {
									type = type + ((char) columnsTableFile.readByte());
								}

								index = (int) columnsTableFile.getFilePointer();
								byte length4 = columnsTableFile.readByte();
								index += 1 + length4;
								columnsTableFile.seek(index);
								byte len = columnsTableFile.readByte();
								if (len == 0) {
									index += 1;
								} else {
									index += 1 + len;
								}
								return column;
							} else {
								index = (int) (columnsTableFile.getFilePointer() + 1 + columnsTableFile.readByte());
								columnsTableFile.seek(index);
								index += 1 + columnsTableFile.readByte();
								columnsTableFile.seek(index);
								byte len = columnsTableFile.readByte();

								if (len == 0) {
									index += 1;
								} else {
									index += 1 + len;
								}
								continue;
							}

						} else {
							index += 1 + varcharLength + 1 + length + 1 + columnsTableFile.readByte();
							columnsTableFile.seek(index);
							index += 4 + columnsTableFile.readByte();
							columnsTableFile.seek(index);
							index += 1 + columnsTableFile.readByte();
							columnsTableFile.seek(index);
							index += 2 + columnsTableFile.readByte();
							continue;
						}
					} else {
						index += 1 + varcharLength + 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 4 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						byte len = columnsTableFile.readByte();

						if (len == 0) {
							index += 1;
						} else {
							index += 1 + len;
						}
					}
				} catch (EOFException e) {
					break;
				}
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return "";
	}

	/**
	 * When a row is inserted, update the .tbl file(Data file) and .ndx file(Index file)
	 */
	
	private static void writeToDataAndIndex(String table, String col, String type, String colname , long writePos) throws IOException {
		RandomAccessFile newTableFile = new RandomAccessFile(inUseSchema + "." + table + ".tbl", "rw");
		RandomAccessFile newIndexFile = new RandomAccessFile(inUseSchema + "." + table + "." + colname + ".ndx", "rw");
		newTableFile.seek(newTableFile.length());


		if (StringUtils.equalsIgnoreCase(type, "byte")) {
			TreeMap<Byte, ArrayList> columnIndex = new TreeMap();
			ArrayList<Long> Addresses = null;

			newTableFile.writeByte(Byte.parseByte(col));

			while (true) {
				try {
					byte putkey = newIndexFile.readByte();
					int num = newIndexFile.readInt();
					Addresses = new ArrayList<Long>();
					for (int i = 0; i < num; i++) {
						Addresses.add(newIndexFile.readLong());
					}
					columnIndex.put(putkey, Addresses);
				} catch (EOFException e) {
					break;
				}
			}
			Addresses = new ArrayList<Long>();
			if (columnIndex.get(Byte.parseByte(col)) == null) {
				columnIndex.put(Byte.parseByte(col), Addresses);
			}
			Addresses = columnIndex.get(Byte.parseByte(col));
			Addresses.add(writePos);
			columnIndex.put(Byte.parseByte(col), Addresses);

			newIndexFile.seek(0);
			for (Entry<Byte, ArrayList> entry : columnIndex.entrySet()) {
				byte key = entry.getKey();
				newIndexFile.writeByte(key);
				ArrayList values = entry.getValue();
				newIndexFile.writeInt(values.size());
				for (int i = 0; i < values.size(); i++) {
					newIndexFile.writeLong((long) values.get(i));
				}

			}
		} else if (StringUtils.equalsIgnoreCase(type, "short int") || StringUtils.equalsIgnoreCase(type, "short")) {
			TreeMap<Short, ArrayList> columnIndex = new TreeMap();
			ArrayList<Long> Addresses = null;

			newTableFile.writeShort(Short.parseShort(col));

			while (true) {
				try {
					short putkey = newIndexFile.readShort();
					int num = newIndexFile.readInt();
					Addresses = new ArrayList<Long>();
					for (int i = 0; i < num; i++) {
						Addresses.add(newIndexFile.readLong());
					}
					columnIndex.put(putkey, Addresses);
				} catch (EOFException e) {
					break;
				}
			}
			Addresses = new ArrayList<Long>();
			if (columnIndex.get(Short.parseShort(col)) == null) {
				columnIndex.put(Short.parseShort(col), Addresses);
			}
			Addresses = columnIndex.get(Short.parseShort(col));
			Addresses.add(writePos);
			columnIndex.put(Short.parseShort(col), Addresses);

			newIndexFile.seek(0);
			for (Entry<Short, ArrayList> entry : columnIndex.entrySet()) {
				short key = entry.getKey();
				newIndexFile.writeShort(key);
				ArrayList values = entry.getValue();
				newIndexFile.writeInt(values.size());
				for (int i = 0; i < values.size(); i++) {
					newIndexFile.writeLong((long) values.get(i));
				}

			}

		}
		else if (StringUtils.equalsIgnoreCase(type, "long int") || StringUtils.equalsIgnoreCase(type, "long")) {
			TreeMap<Long, ArrayList> columnIndex = new TreeMap();
			ArrayList<Long> Addresses = null;

			newTableFile.writeLong(Long.parseLong(col));

			while (true) {
				try {
					long putkey = newIndexFile.readLong();
					int num = newIndexFile.readInt();
					Addresses = new ArrayList<Long>();
					for (int i = 0; i < num; i++) {
						Addresses.add(newIndexFile.readLong());
					}
					columnIndex.put(putkey, Addresses);
				} catch (EOFException e) {
					break;
				}
			}
			Addresses = new ArrayList<Long>();
			if (columnIndex.get(Long.parseLong(col)) == null) {
				columnIndex.put(Long.parseLong(col), Addresses);
			}
			Addresses = columnIndex.get(Long.parseLong(col));
			Addresses.add(writePos);
			columnIndex.put(Long.parseLong(col), Addresses);

			newIndexFile.seek(0);
			for (Entry<Long, ArrayList> entry : columnIndex.entrySet()) {
				long key = entry.getKey();
				newIndexFile.writeLong(key);
				ArrayList values = entry.getValue();
				newIndexFile.writeInt(values.size());
				for (int i = 0; i < values.size(); i++) {
					newIndexFile.writeLong((long) values.get(i));
				}

			}

		} else if (StringUtils.equalsIgnoreCase(type, "int")) {
			TreeMap<Integer, ArrayList> columnIndex = new TreeMap();
			ArrayList<Long> Addresses = null;

			newTableFile.writeInt(Integer.parseInt(col));

			while (true) {
				try {
					int putkey = newIndexFile.readInt();
					int num = newIndexFile.readInt();
					Addresses = new ArrayList<Long>();
					for (int i = 0; i < num; i++) {
						Addresses.add(newIndexFile.readLong());
					}
					columnIndex.put(putkey, Addresses);
				} catch (EOFException e) {
					break;
				}
			}
			Addresses = new ArrayList<Long>();
			if (columnIndex.get(Integer.parseInt(col)) == null) {
				columnIndex.put(Integer.parseInt(col), Addresses);
			}
			Addresses = columnIndex.get(Integer.parseInt(col));
			Addresses.add(writePos);
			columnIndex.put(Integer.parseInt(col), Addresses);

			newIndexFile.seek(0);
			for (Entry<Integer, ArrayList> entry : columnIndex.entrySet()) {
				int key = entry.getKey();
				newIndexFile.writeInt(key);
				ArrayList values = entry.getValue();
				newIndexFile.writeInt(values.size());
				for (int i = 0; i < values.size(); i++) {
					newIndexFile.writeLong((long) values.get(i));
				}

			}

		} else if (StringUtils.containsIgnoreCase(type, "varchar(")) {

			TreeMap<String, ArrayList> columnIndex = new TreeMap();
			ArrayList<Long> Addresses = null;

			newTableFile.writeByte(col.length());
			newTableFile.writeBytes(col);

			while (true) {
				try {
					byte len = newIndexFile.readByte();
					String putkey = "";
					for (int i = 0; i < len; i++) {
						putkey = putkey + String.valueOf((char) newIndexFile.readByte());
					}
					int num = newIndexFile.readInt();
					Addresses = new ArrayList<Long>();
					for (int i = 0; i < num; i++) {
						Addresses.add(newIndexFile.readLong());
					}
					columnIndex.put(putkey, Addresses);
				} catch (EOFException e) {
					break;
				}
			}
			Addresses = new ArrayList<Long>();
			if (columnIndex.get(col) == null) {
				columnIndex.put(col, Addresses);
			}
			Addresses = columnIndex.get(col);
			Addresses.add(writePos);
			columnIndex.put(col, Addresses);

			newIndexFile.seek(0);
			for (Entry<String, ArrayList> entry : columnIndex.entrySet()) {
				String key = entry.getKey();
				newIndexFile.writeByte(key.length());
				newIndexFile.writeBytes(key);
				ArrayList values = entry.getValue();
				newIndexFile.writeInt(values.size());
				for (int i = 0; i < values.size(); i++) {
					newIndexFile.writeLong((long) values.get(i));
				}
			}

		} else if (StringUtils.equalsIgnoreCase(type, "char")) {

			TreeMap<Character, ArrayList> columnIndex = new TreeMap();
			ArrayList<Long> Addresses = null;

			newTableFile.writeChar(col.charAt(0));

			while (true) {
				try {
					char putkey = newIndexFile.readChar();
					int num = newIndexFile.readInt();
					Addresses = new ArrayList<Long>();
					for (int i = 0; i < num; i++) {
						Addresses.add(newIndexFile.readLong());
					}
					columnIndex.put(putkey, Addresses);
				} catch (EOFException e) {
					break;
				}
			}
			Addresses = new ArrayList<Long>();
			if (columnIndex.get(col.charAt(0)) == null) {
				columnIndex.put(col.charAt(0), Addresses);
			}
			Addresses = columnIndex.get(col.charAt(0));
			Addresses.add(writePos);
			columnIndex.put(col.charAt(0), Addresses);

			newIndexFile.seek(0);
			for (Entry<Character, ArrayList> entry : columnIndex.entrySet()) {
				char key = entry.getKey();
				newIndexFile.writeChar(key);
				ArrayList values = entry.getValue();
				newIndexFile.writeInt(values.size());
				for (int i = 0; i < values.size(); i++) {
					newIndexFile.writeLong((long) values.get(i));
				}

			}

		}

		else if (StringUtils.equalsIgnoreCase(type, "float")) {

			TreeMap<Float, ArrayList> columnIndex = new TreeMap();
			ArrayList<Long> Addresses = null;

			newTableFile.writeFloat(Float.parseFloat(col));

			while (true) {
				try {
					float putkey = newIndexFile.readFloat();
					int num = newIndexFile.readInt();
					Addresses = new ArrayList<Long>();
					for (int i = 0; i < num; i++) {
						Addresses.add(newIndexFile.readLong());
					}
					columnIndex.put(putkey, Addresses);
				} catch (EOFException e) {
					break;
				}
			}
			Addresses = new ArrayList<Long>();
			if (columnIndex.get(Float.parseFloat(col)) == null) {
				columnIndex.put(Float.parseFloat(col), Addresses);
			}
			Addresses = columnIndex.get(Float.parseFloat(col));
			Addresses.add(writePos);
			columnIndex.put(Float.parseFloat(col), Addresses);

			newIndexFile.seek(0);
			for (Entry<Float, ArrayList> entry : columnIndex.entrySet()) {
				float key = entry.getKey();
				newIndexFile.writeFloat(key);
				ArrayList values = entry.getValue();
				newIndexFile.writeInt(values.size());
				for (int i = 0; i < values.size(); i++) {
					newIndexFile.writeLong((long) values.get(i));
				}

			}

		} else if (StringUtils.equalsIgnoreCase(type, "double")) {

			TreeMap<Double, ArrayList> columnIndex = new TreeMap();
			ArrayList<Long> Addresses = null;

			newTableFile.writeDouble(Double.parseDouble(col));

			while (true) {
				try {
					double putkey = newIndexFile.readDouble();
					int num = newIndexFile.readInt();
					Addresses = new ArrayList<Long>();
					for (int i = 0; i < num; i++) {
						Addresses.add(newIndexFile.readLong());
					}
					columnIndex.put(putkey, Addresses);
				} catch (EOFException e) {
					break;
				}
			}
			Addresses = new ArrayList<Long>();
			if (columnIndex.get(Double.parseDouble(col)) == null) {
				columnIndex.put(Double.parseDouble(col), Addresses);
			}
			Addresses = columnIndex.get(Double.parseDouble(col));
			Addresses.add(writePos);
			columnIndex.put(Double.parseDouble(col), Addresses);

			newIndexFile.seek(0);
			for (Entry<Double, ArrayList> entry : columnIndex.entrySet()) {
				double key = entry.getKey();
				newIndexFile.writeDouble(key);
				ArrayList values = entry.getValue();
				newIndexFile.writeInt(values.size());
				for (int i = 0; i < values.size(); i++) {
					newIndexFile.writeLong((long) values.get(i));
				}

			}

		} 
//		else if (StringUtils.equalsIgnoreCase(type, "datetime")) {
//			newTableFile.writeUTF(col);
//
//		} else if (StringUtils.equalsIgnoreCase(type, "date")) {
//			newTableFile.writeUTF(col);
//
//		} 
		else {
			System.out.println("Syntax error in the values");
		}
	}

	/**
	 * Select the rows, from a table, that satisfy a condition on a column values
	 */
	
	private static void selectRows(String table, String col, String value) {
		try {
			int indexFileLocation = 0;
			long indexOfRecord = 0;
			boolean recordFound = false;

			RandomAccessFile indexFile = new RandomAccessFile(inUseSchema + "." + table + "." + col + ".ndx", "rw");
			ArrayList<Long> addresses = new ArrayList<>();


			String str = getColumnType(table, col);

			if (StringUtils.equalsIgnoreCase(str, "byte")) {
				int count = 0;
				
				while (!recordFound) {
					indexFile.seek(indexFileLocation);
					if (indexFile.readByte() == Byte.parseByte(value)) {
						indexFile.seek(indexFileLocation + 4);
						count = indexFile.readInt();
						for(int i = 0; i<count ; i++){
							addresses.add(indexFile.readLong());
						}
						recordFound = true;
					}
					indexFileLocation +=  1 + 4 + (count * 8);
				}


			} 
			else if (StringUtils.equalsIgnoreCase(str, "short int") || StringUtils.equalsIgnoreCase(str, "short")) {
				int count = 0;
				
				while (!recordFound) {
					indexFile.seek(indexFileLocation);
					if (indexFile.readShort() == Short.parseShort(value)) {
						indexFile.seek(indexFileLocation + 2);
						count = indexFile.readInt();
						for(int i = 0; i<count ; i++){
							addresses.add(indexFile.readLong());
						}
						recordFound = true;
					}
					indexFileLocation +=  2 + 4 + (count * 8);
				}


			} 
			else if (StringUtils.equalsIgnoreCase(str, "long int") || StringUtils.equalsIgnoreCase(str, "long")) {
				int count = 0;
				
				while (!recordFound) {
					indexFile.seek(indexFileLocation);
					if (indexFile.readLong() == Long.parseLong(value)) {
						indexFile.seek(indexFileLocation + 8);
						count = indexFile.readInt();
						for(int i = 0; i<count ; i++){
							addresses.add(indexFile.readLong());
						}
						recordFound = true;
					}
					indexFileLocation +=  8 + 4 + (count * 8);
				}


			}  
			else if (StringUtils.equalsIgnoreCase(str, "int")) {
				int count = 0;
				
				while (!recordFound) {
					indexFile.seek(indexFileLocation);
					if (indexFile.readInt() == Integer.parseInt(value)) {
						indexFile.seek(indexFileLocation + 4);
						count = indexFile.readInt();
						for(int i = 0; i<count ; i++){
							addresses.add(indexFile.readLong());
						}
						recordFound = true;
					}
					indexFileLocation +=  4 + 4 + (count * 8);
				}

			} 
			else if (StringUtils.equalsIgnoreCase(str, "char")) {

			} 
			else if (StringUtils.containsIgnoreCase(str, "varchar")) {
				int count = 0;
				while (!recordFound) {
					indexFile.seek(indexFileLocation);
					byte len = indexFile.readByte();
					String val = "";
					for(int i = 0 ; i < len ; i++){
						val = val + String.valueOf((char) indexFile.readByte());
					}
					if (val.equals(value)) {
						indexFile.seek(indexFileLocation + 1 + len);
						count = indexFile.readInt();
						
						for(int i = 0; i<count ; i++){
							addresses.add(indexFile.readLong());
						}
						recordFound = true;
					}
					else{
						indexFile.seek(indexFileLocation + 1 + len);
						count = indexFile.readInt();
					}
					indexFileLocation +=  1 + len + 4 + (count * 8);
				}

			} 
			else if (StringUtils.equalsIgnoreCase(str, "float")) {
				int count = 0;
				
				while (!recordFound) {
					indexFile.seek(indexFileLocation);
					if (indexFile.readFloat() == Float.parseFloat(value)) {
						indexFile.seek(indexFileLocation + 4);
						count = indexFile.readInt();
						for(int i = 0; i<count ; i++){
							addresses.add(indexFile.readLong());
						}
						recordFound = true;
					}
					indexFileLocation +=  4 + 4 + (count * 8);
					
				}

			} 
			else if (StringUtils.equalsIgnoreCase(str, "double")) {
				int count = 0;
				
				while (!recordFound) {
					indexFile.seek(indexFileLocation);
					if (indexFile.readDouble() == Double.parseDouble(value)) {
						indexFile.seek(indexFileLocation + 8);
						count = indexFile.readInt();
						for(int i = 0; i<count ; i++){
							addresses.add(indexFile.readLong());
						}
						recordFound = true;
					}
					indexFileLocation +=  8 + 4 + (count * 8);
				}


			} 
//			else if (StringUtils.equalsIgnoreCase(str, "datetime")) {
//
//			} 
//			else if (StringUtils.equalsIgnoreCase(str, "datetime")) {
//
//			}
			readSelectTableFile(table, addresses);
			}
		catch (Exception e) {
			System.out.println(e);
		}
	}
	
	/**
	 * Display the content, of selected rows, from the .tbl file(Data file)
	 */
	
	private static void readSelectTableFile(String table , ArrayList<Long> addresses) throws IOException{
		try {
			RandomAccessFile tableFile = new RandomAccessFile(inUseSchema + "." + table + ".tbl", "rw");
			long indexOfRecord = 0;
			ArrayList<String> types = getColumnType(table);
			int columns = types.size();
			for(int i = 0 ; i < addresses.size() ; i++){
				indexOfRecord = addresses.get(i);
				tableFile.seek(indexOfRecord);
				for(int j = 0 ; j < columns ; j++){
					String type = types.get(j);
					if(type.equalsIgnoreCase("byte")){
						System.out.print(tableFile.readByte());
						System.out.print(" -- ");
					}
					else if(type.equalsIgnoreCase("int")){
						System.out.print(tableFile.readInt());
						System.out.print(" -- ");
					}
					else if(type.equalsIgnoreCase("long") || type.equalsIgnoreCase("long int")){
						System.out.print(tableFile.readLong());
						System.out.print(" -- ");
					}
					else if(StringUtils.containsIgnoreCase(type, "varchar")){
						byte varcharLength = tableFile.readByte();
						for (int len = 0; len < varcharLength; len++){
							System.out.print(String.valueOf((char) tableFile.readByte()));
						}	
						System.out.print(" -- ");
					}
					else if(type.equalsIgnoreCase("short") || type.equalsIgnoreCase("short int")){
						System.out.print(tableFile.readShort());
						System.out.print(" -- ");
					}
					else if(type.equalsIgnoreCase("float")){
						System.out.print(tableFile.readFloat());
						System.out.print(" -- ");
					}
					else if(type.equalsIgnoreCase("double")){
						System.out.print(tableFile.readDouble());
						System.out.print(" -- ");
					}
				}
				System.out.println();
			}
			System.out.println();
				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch(EOFException e1){
			return;
		}
	}

	/**
	 * Get the PRIMARY/NOT NULL properties of a column
	 */
	
	private static String[] getColumnProperties(String inUseTable, String col) throws IOException {
		try {
			String[] prop = new String[2];
			RandomAccessFile columnsTableFile = new RandomAccessFile(columnsTable, "rw");
			int index = 0;

			while (true) {
				String type = "";
				try {
					columnsTableFile.seek(index);
					String schema = "";
					byte varcharLength = columnsTableFile.readByte();
					for (int i = 0; i < varcharLength; i++) {
						schema = schema + String.valueOf((char) columnsTableFile.readByte());
					}

					if (schema.equals(inUseSchema)) {
						byte length = columnsTableFile.readByte();
						String table = "";
						for (int i = 0; i < length; i++) {
							table = table + String.valueOf((char) columnsTableFile.readByte());
						}

						if (table.equals(inUseTable)) {
							byte length1 = columnsTableFile.readByte();
							String column = "";
							for (int i = 0; i < length1; i++) {
								column = column + String.valueOf((char) columnsTableFile.readByte());
							}

							columnsTableFile.readInt();
							byte length2 = columnsTableFile.readByte();

							for (int i = 0; i < length2; i++) {
								type = type + String.valueOf((char) columnsTableFile.readByte());
							}
							byte length4 = columnsTableFile.readByte();
							String nullable = "";
							for(int i = 0 ; i < length4 ; i++){
								nullable = nullable + String.valueOf((char) columnsTableFile.readByte());
							}
							prop[0] = nullable;
							index = (int) columnsTableFile.getFilePointer();
							columnsTableFile.seek(index);
							byte len = columnsTableFile.readByte();
							if ((int) len == 0) {
								index += 1;
								prop[1] = "NOT_PRIM";
							} else {
								String prim = "";
								for(int i = 0 ; i < len ; i++){
									prim = prim + String.valueOf((char) columnsTableFile.readByte());
								}
								prop[1] = prim;
								index += 1 + len;
							}

							if (column.equals(col)) {
								return prop;
							} else {
								continue;
							}

						} else {
							index += 1 + varcharLength + 1 + length + 1 + columnsTableFile.readByte();
							columnsTableFile.seek(index);
							index += 4 + columnsTableFile.readByte();
							columnsTableFile.seek(index);
							index += 1 + columnsTableFile.readByte();
							columnsTableFile.seek(index);
							index += 2 + columnsTableFile.readByte();
							continue;
						}
					} else {
						index += 1 + varcharLength + 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 4 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						index += 1 + columnsTableFile.readByte();
						columnsTableFile.seek(index);
						byte len = columnsTableFile.readByte();

						if (len == 0) {
							index += 1;
						} else {
							index += 1 + len;
						}
					}
				} catch (EOFException e) {
					break;
				}
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Get the count of number of columns in a table
	 */
	
	private static int getColumnCount(String table){
		try {
			return getColumnType(table).size();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * When a new row is inserted into a table, update the count of number of rows in information_schema.table.tbl file
	 */
	
	private static void updateRowCountInTables(String inUseTable) throws IOException{
		try {
			RandomAccessFile tablesTableFile = new RandomAccessFile(tablesTable, "rw");
			int index = 0;
			while (true) {
				try {
					String schema = "";
					tablesTableFile.seek(index);
					byte varcharLength = tablesTableFile.readByte();
					for (int i = 0; i < varcharLength; i++) {
						schema = schema + String.valueOf((char) tablesTableFile.readByte());
					}
					if (schema.equalsIgnoreCase(inUseSchema)) {
						byte length = tablesTableFile.readByte();
						String table = "";
						for (int i = 0; i < length; i++) {
							table = table + (char) tablesTableFile.readByte();
						}
						if(table.equalsIgnoreCase(inUseTable)){
							long pos = tablesTableFile.getFilePointer();
							long count = tablesTableFile.readLong();
							count++;
							tablesTableFile.seek(pos);
							tablesTableFile.writeLong(count);
							index = index + varcharLength + length + 2 + 8;
						}
						else{
							index = index + varcharLength + length + 2 + 8;
						}
					} else {
						byte length = tablesTableFile.readByte();
						index = index + varcharLength + length + 2 + 8;
					}
				} catch (EOFException e) {
					break;
				}
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Find if inserting a new row violates Primary key constraint(if exists)
	 */
	
	private static boolean isPrimaryViolated(String table, String col, String type, String colname) throws IOException{
		
		RandomAccessFile newTableFile = new RandomAccessFile(inUseSchema + "." + table + ".tbl", "rw");
		RandomAccessFile newIndexFile = new RandomAccessFile(inUseSchema + "." + table + "." + colname + ".ndx", "rw");
		newTableFile.seek(newTableFile.length());


		if (StringUtils.equalsIgnoreCase(type, "byte")) {
			TreeMap<Byte, ArrayList> columnIndex = new TreeMap();
			ArrayList<Long> Addresses = null;

			while (true) {
				try {
					byte putkey = newIndexFile.readByte();
					int num = newIndexFile.readInt();
					Addresses = new ArrayList<Long>();
					for (int i = 0; i < num; i++) {
						Addresses.add(newIndexFile.readLong());
					}
					columnIndex.put(putkey, Addresses);
				} catch (EOFException e) {
					break;
				}
			}
			Addresses = new ArrayList<Long>();
			if (columnIndex.get(Byte.parseByte(col)) != null) {
				System.out.println("Reject");
				return true;
			}
			else{
				System.out.println("Accept");
				return false;
			}
			
		} else if (StringUtils.equalsIgnoreCase(type, "short int") || StringUtils.equalsIgnoreCase(type, "short")) {
			TreeMap<Short, ArrayList> columnIndex = new TreeMap();
			ArrayList<Long> Addresses = null;

			while (true) {
				try {
					short putkey = newIndexFile.readShort();
					int num = newIndexFile.readInt();
					Addresses = new ArrayList<Long>();
					for (int i = 0; i < num; i++) {
						Addresses.add(newIndexFile.readLong());
					}
					columnIndex.put(putkey, Addresses);
				} catch (EOFException e) {
					break;
				}
			}
			Addresses = new ArrayList<Long>();
			if (columnIndex.get(Short.parseShort(col)) != null) {
				return true;
			}
			else
				return false;
		}

		else if (StringUtils.equalsIgnoreCase(type, "long int") || StringUtils.equalsIgnoreCase(type, "long")) {
			TreeMap<Long, ArrayList> columnIndex = new TreeMap();
			ArrayList<Long> Addresses = null;

			while (true) {
				try {
					long putkey = newIndexFile.readLong();
					int num = newIndexFile.readInt();
					Addresses = new ArrayList<Long>();
					for (int i = 0; i < num; i++) {
						Addresses.add(newIndexFile.readLong());
					}
					columnIndex.put(putkey, Addresses);
				} catch (EOFException e) {
					break;
				}
			}

			Addresses = new ArrayList<Long>();
			if (columnIndex.get(Long.parseLong(col)) != null) {
				return true;
			}
			else
				return false;

		} else if (StringUtils.equalsIgnoreCase(type, "int")) {
			TreeMap<Integer, ArrayList> columnIndex = new TreeMap();
			ArrayList<Long> Addresses = null;

			while (true) {
				try {
					int putkey = newIndexFile.readInt();
					int num = newIndexFile.readInt();
					Addresses = new ArrayList<Long>();
					for (int i = 0; i < num; i++) {
						Addresses.add(newIndexFile.readLong());
					}
					columnIndex.put(putkey, Addresses);
				} catch (EOFException e) {
					break;
				}
			}

			Addresses = new ArrayList<Long>();
			if (columnIndex.get(Integer.parseInt(col)) != null) {
				return true;
			}
			else
				return false;
			
		} else if (StringUtils.containsIgnoreCase(type, "varchar(")) {

			TreeMap<String, ArrayList> columnIndex = new TreeMap();
			ArrayList<Long> Addresses = null;

			while (true) {
				try {
					byte len = newIndexFile.readByte();
					String putkey = "";
					for (int i = 0; i < len; i++) {
						putkey = putkey + String.valueOf((char) newIndexFile.readByte());
					}
					int num = newIndexFile.readInt();
					Addresses = new ArrayList<Long>();
					for (int i = 0; i < num; i++) {
						Addresses.add(newIndexFile.readLong());
					}
					columnIndex.put(putkey, Addresses);
				} catch (EOFException e) {
					break;
				}
			}
			Addresses = new ArrayList<Long>();
			if (columnIndex.get(col) != null) {
				return true;
			}
			else
				return false;

		} else if (StringUtils.equalsIgnoreCase(type, "char")) {

			TreeMap<Character, ArrayList> columnIndex = new TreeMap();
			ArrayList<Long> Addresses = null;

			while (true) {
				try {
					char putkey = newIndexFile.readChar();
					int num = newIndexFile.readInt();
					Addresses = new ArrayList<Long>();
					for (int i = 0; i < num; i++) {
						Addresses.add(newIndexFile.readLong());
					}
					columnIndex.put(putkey, Addresses);
				} catch (EOFException e) {
					break;
				}
			}
			Addresses = new ArrayList<Long>();
			if (columnIndex.get(col.charAt(0)) != null) {
				return true;
			}
			else
				return false;
		}
		else if (StringUtils.equalsIgnoreCase(type, "float")) {

			TreeMap<Float, ArrayList> columnIndex = new TreeMap();
			ArrayList<Long> Addresses = null;

			while (true) {
				try {
					float putkey = newIndexFile.readFloat();
					int num = newIndexFile.readInt();
					Addresses = new ArrayList<Long>();
					for (int i = 0; i < num; i++) {
						Addresses.add(newIndexFile.readLong());
					}
					columnIndex.put(putkey, Addresses);
				} catch (EOFException e) {
					break;
				}
			}
			Addresses = new ArrayList<Long>();
			if (columnIndex.get(Float.parseFloat(col)) != null) {
				return true;
			}
			else
				return false;
		} else if (StringUtils.equalsIgnoreCase(type, "double")) {

			TreeMap<Double, ArrayList> columnIndex = new TreeMap();
			ArrayList<Long> Addresses = null;
			while (true) {
				try {
					double putkey = newIndexFile.readDouble();
					int num = newIndexFile.readInt();
					Addresses = new ArrayList<Long>();
					for (int i = 0; i < num; i++) {
						Addresses.add(newIndexFile.readLong());
					}
					columnIndex.put(putkey, Addresses);
				} catch (EOFException e) {
					break;
				}
			}
			Addresses = new ArrayList<Long>();
			if (columnIndex.get(Double.parseDouble(col)) != null) {
				return true;
			}
			else
				return false;

		} 
//		else if (StringUtils.equalsIgnoreCase(type, "datetime")) {
//			//newTableFile.writeUTF(col);
//
//		} else if (StringUtils.equalsIgnoreCase(type, "date")) {
//			//newTableFile.writeUTF(col);
//
//		} 
		return false;
	}
	
	/**
	 * Create initial Information_Schema
	 */
	
	public static void makeInfromationSchema() {
		// System.out.println("Creating Information Schema");
		try {
			/*
			 * FIXME: Put all binary data files in a separate subdirectory
			 * (subdirectory tree?)
			 */
			/*
			 * FIXME: Should there not be separate Class static variables for
			 * the file names? and just hard code them here?
			 */
			/*
			 * TODO: Should there be separate methods to checkfor and
			 * subsequently create each file granularly, instead of a big bang
			 * all or nothing?
			 */
			schemataTableFile = new RandomAccessFile("information_schema.schemata.tbl", "rw");
			RandomAccessFile tablesTableFile = new RandomAccessFile("information_schema.table.tbl", "rw");
			RandomAccessFile columnsTableFile = new RandomAccessFile("information_schema.columns.tbl", "rw");

			/*
			 * Create the SCHEMATA table file. Initially it has only one entry:
			 * information_schema
			 */
			// ROW 1: information_schema.schemata.tbl
			schemataTableFile.writeByte("information_schema".length());
			schemataTableFile.writeBytes("information_schema");

			/*
			 * Create the TABLES table file. Remember!!! Column names are not
			 * stored in the tables themselves The column names (TABLE_SCHEMA,
			 * TABLE_NAME, TABLE_ROWS) and their order (ORDINAL_POSITION) are
			 * encoded in the COLUMNS table. Initially it has three rows (each
			 * row may have a different length):
			 */
			// ROW 1: information_schema.tables.tbl
			tablesTableFile.writeByte("information_schema".length()); // TABLE_SCHEMA
			tablesTableFile.writeBytes("information_schema");
			tablesTableFile.writeByte("SCHEMATA".length()); // TABLE_NAME
			tablesTableFile.writeBytes("SCHEMATA");
			tablesTableFile.writeLong(1); // TABLE_ROWS

			// ROW 2: information_schema.tables.tbl
			tablesTableFile.writeByte("information_schema".length()); // TABLE_SCHEMA
			tablesTableFile.writeBytes("information_schema");
			tablesTableFile.writeByte("TABLES".length()); // TABLE_NAME
			tablesTableFile.writeBytes("TABLES");
			tablesTableFile.writeLong(3); // TABLE_ROWS

			// ROW 3: information_schema.tables.tbl
			tablesTableFile.writeByte("information_schema".length()); // TABLE_SCHEMA
			tablesTableFile.writeBytes("information_schema");
			tablesTableFile.writeByte("COLUMNS".length()); // TABLE_NAME
			tablesTableFile.writeBytes("COLUMNS");
			tablesTableFile.writeLong(7); // TABLE_ROWS

			/*
			 * Create the COLUMNS table file. Initially it has 11 rows:
			 */
			// ROW 1: information_schema.columns.tbl
			columnsTableFile.writeByte("information_schema".length()); // TABLE_SCHEMA
			columnsTableFile.writeBytes("information_schema");
			columnsTableFile.writeByte("SCHEMATA".length()); // TABLE_NAME
			columnsTableFile.writeBytes("SCHEMATA");
			columnsTableFile.writeByte("SCHEMA_NAME".length()); // COLUMN_NAME
			columnsTableFile.writeBytes("SCHEMA_NAME");
			columnsTableFile.writeInt(1); // ORDINAL_POSITION
			columnsTableFile.writeByte("varchar(64)".length()); // COLUMN_TYPE
			columnsTableFile.writeBytes("varchar(64)");
			columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
			columnsTableFile.writeBytes("NO");
			columnsTableFile.writeByte("".length()); // COLUMN_KEY
			columnsTableFile.writeBytes("");

			// ROW 2: information_schema.columns.tbl
			columnsTableFile.writeByte("information_schema".length()); // TABLE_SCHEMA
			columnsTableFile.writeBytes("information_schema");
			columnsTableFile.writeByte("TABLES".length()); // TABLE_NAME
			columnsTableFile.writeBytes("TABLES");
			columnsTableFile.writeByte("TABLE_SCHEMA".length()); // COLUMN_NAME
			columnsTableFile.writeBytes("TABLE_SCHEMA");
			columnsTableFile.writeInt(1); // ORDINAL_POSITION
			columnsTableFile.writeByte("varchar(64)".length()); // COLUMN_TYPE
			columnsTableFile.writeBytes("varchar(64)");
			columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
			columnsTableFile.writeBytes("NO");
			columnsTableFile.writeByte("".length()); // COLUMN_KEY
			columnsTableFile.writeBytes("");

			// ROW 3: information_schema.columns.tbl
			columnsTableFile.writeByte("information_schema".length()); // TABLE_SCHEMA
			columnsTableFile.writeBytes("information_schema");
			columnsTableFile.writeByte("TABLES".length()); // TABLE_NAME
			columnsTableFile.writeBytes("TABLES");
			columnsTableFile.writeByte("TABLE_NAME".length()); // COLUMN_NAME
			columnsTableFile.writeBytes("TABLE_NAME");
			columnsTableFile.writeInt(2); // ORDINAL_POSITION
			columnsTableFile.writeByte("varchar(64)".length()); // COLUMN_TYPE
			columnsTableFile.writeBytes("varchar(64)");
			columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
			columnsTableFile.writeBytes("NO");
			columnsTableFile.writeByte("".length()); // COLUMN_KEY
			columnsTableFile.writeBytes("");

			// ROW 4: information_schema.columns.tbl
			columnsTableFile.writeByte("information_schema".length()); // TABLE_SCHEMA
			columnsTableFile.writeBytes("information_schema");
			columnsTableFile.writeByte("TABLES".length()); // TABLE_NAME
			columnsTableFile.writeBytes("TABLES");
			columnsTableFile.writeByte("TABLE_ROWS".length()); // COLUMN_NAME
			columnsTableFile.writeBytes("TABLE_ROWS");
			columnsTableFile.writeInt(3); // ORDINAL_POSITION
			columnsTableFile.writeByte("long int".length()); // COLUMN_TYPE
			columnsTableFile.writeBytes("long int");
			columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
			columnsTableFile.writeBytes("NO");
			columnsTableFile.writeByte("".length()); // COLUMN_KEY
			columnsTableFile.writeBytes("");

			// ROW 5: information_schema.columns.tbl
			columnsTableFile.writeByte("information_schema".length()); // TABLE_SCHEMA
			columnsTableFile.writeBytes("information_schema");
			columnsTableFile.writeByte("COLUMNS".length()); // TABLE_NAME
			columnsTableFile.writeBytes("COLUMNS");
			columnsTableFile.writeByte("TABLE_SCHEMA".length()); // COLUMN_NAME
			columnsTableFile.writeBytes("TABLE_SCHEMA");
			columnsTableFile.writeInt(1); // ORDINAL_POSITION
			columnsTableFile.writeByte("varchar(64)".length()); // COLUMN_TYPE
			columnsTableFile.writeBytes("varchar(64)");
			columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
			columnsTableFile.writeBytes("NO");
			columnsTableFile.writeByte("".length()); // COLUMN_KEY
			columnsTableFile.writeBytes("");

			// ROW 6: information_schema.columns.tbl
			columnsTableFile.writeByte("information_schema".length()); // TABLE_SCHEMA
			columnsTableFile.writeBytes("information_schema");
			columnsTableFile.writeByte("COLUMNS".length()); // TABLE_NAME
			columnsTableFile.writeBytes("COLUMNS");
			columnsTableFile.writeByte("TABLE_NAME".length()); // COLUMN_NAME
			columnsTableFile.writeBytes("TABLE_NAME");
			columnsTableFile.writeInt(2); // ORDINAL_POSITION
			columnsTableFile.writeByte("varchar(64)".length()); // COLUMN_TYPE
			columnsTableFile.writeBytes("varchar(64)");
			columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
			columnsTableFile.writeBytes("NO");
			columnsTableFile.writeByte("".length()); // COLUMN_KEY
			columnsTableFile.writeBytes("");

			// ROW 7: information_schema.columns.tbl
			columnsTableFile.writeByte("information_schema".length()); // TABLE_SCHEMA
			columnsTableFile.writeBytes("information_schema");
			columnsTableFile.writeByte("COLUMNS".length()); // TABLE_NAME
			columnsTableFile.writeBytes("COLUMNS");
			columnsTableFile.writeByte("COLUMN_NAME".length()); // COLUMN_NAME
			columnsTableFile.writeBytes("COLUMN_NAME");
			columnsTableFile.writeInt(3); // ORDINAL_POSITION
			columnsTableFile.writeByte("varchar(64)".length()); // COLUMN_TYPE
			columnsTableFile.writeBytes("varchar(64)");
			columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
			columnsTableFile.writeBytes("NO");
			columnsTableFile.writeByte("".length()); // COLUMN_KEY
			columnsTableFile.writeBytes("");

			// ROW 8: information_schema.columns.tbl
			columnsTableFile.writeByte("information_schema".length()); // TABLE_SCHEMA
			columnsTableFile.writeBytes("information_schema");
			columnsTableFile.writeByte("COLUMNS".length()); // TABLE_NAME
			columnsTableFile.writeBytes("COLUMNS");
			columnsTableFile.writeByte("ORDINAL_POSITION".length()); // COLUMN_NAME
			columnsTableFile.writeBytes("ORDINAL_POSITION");
			columnsTableFile.writeInt(4); // ORDINAL_POSITION
			columnsTableFile.writeByte("int".length()); // COLUMN_TYPE
			columnsTableFile.writeBytes("int");
			columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
			columnsTableFile.writeBytes("NO");
			columnsTableFile.writeByte("".length()); // COLUMN_KEY
			columnsTableFile.writeBytes("");

			// ROW 9: information_schema.columns.tbl
			columnsTableFile.writeByte("information_schema".length()); // TABLE_SCHEMA
			columnsTableFile.writeBytes("information_schema");
			columnsTableFile.writeByte("COLUMNS".length()); // TABLE_NAME
			columnsTableFile.writeBytes("COLUMNS");
			columnsTableFile.writeByte("COLUMN_TYPE".length()); // COLUMN_NAME
			columnsTableFile.writeBytes("COLUMN_TYPE");
			columnsTableFile.writeInt(5); // ORDINAL_POSITION
			columnsTableFile.writeByte("varchar(64)".length()); // COLUMN_TYPE
			columnsTableFile.writeBytes("varchar(64)");
			columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
			columnsTableFile.writeBytes("NO");
			columnsTableFile.writeByte("".length()); // COLUMN_KEY
			columnsTableFile.writeBytes("");

			// ROW 10: information_schema.columns.tbl
			columnsTableFile.writeByte("information_schema".length()); // TABLE_SCHEMA
			columnsTableFile.writeBytes("information_schema");
			columnsTableFile.writeByte("COLUMNS".length()); // TABLE_NAME
			columnsTableFile.writeBytes("COLUMNS");
			columnsTableFile.writeByte("IS_NULLABLE".length()); // COLUMN_NAME
			columnsTableFile.writeBytes("IS_NULLABLE");
			columnsTableFile.writeInt(6); // ORDINAL_POSITION
			columnsTableFile.writeByte("varchar(3)".length()); // COLUMN_TYPE
			columnsTableFile.writeBytes("varchar(3)");
			columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
			columnsTableFile.writeBytes("NO");
			columnsTableFile.writeByte("".length()); // COLUMN_KEY
			columnsTableFile.writeBytes("");

			// ROW 11: information_schema.columns.tbl
			columnsTableFile.writeByte("information_schema".length()); // TABLE_SCHEMA
			columnsTableFile.writeBytes("information_schema");
			columnsTableFile.writeByte("COLUMNS".length()); // TABLE_NAME
			columnsTableFile.writeBytes("COLUMNS");
			columnsTableFile.writeByte("COLUMN_KEY".length()); // COLUMN_NAME
			columnsTableFile.writeBytes("COLUMN_KEY");
			columnsTableFile.writeInt(7); // ORDINAL_POSITION
			columnsTableFile.writeByte("varchar(3)".length()); // COLUMN_TYPE
			columnsTableFile.writeBytes("varchar(3)");
			columnsTableFile.writeByte("NO".length()); // IS_NULLABLE
			columnsTableFile.writeBytes("NO");
			columnsTableFile.writeByte("".length()); // COLUMN_KEY
			columnsTableFile.writeBytes("");

		} catch (Exception e) {
			System.out.println(e);
		}
	}

}

