package grade

import org.junit.jupiter.api.*

import grade.AbstractModule
import core.Server
import model.Response
import model.Table

class Module1 extends AbstractModule {
	@BeforeAll
	static void setup() {
		module_tag = 'M1'
		
		query_data = [
			// CREATE TABLE, GENERAL
			[ true,  'CREATE TABLE table_01 (id INTEGER PRIMARY, name STRING, flag BOOLEAN)', null ],
			
			// CREATE TABLE, SYNTAX: CASE AND WHITESPACE
			[ true,  'create table table_02 (ID integer primary, NAME string, flag BOOLEAN)', 'lowercase keywords and uppercase table names are allowed' ],
			[ true,  ' CREATE TABLE table_03 (id INTEGER PRIMARY, name STRING, flag BOOLEAN) ', 'untrimmed whitespace is allowed' ],
			[ true,  'CREATE  TABLE  table_04  (id INTEGER PRIMARY, name STRING, flag BOOLEAN)', 'excess internal whitespace is allowed' ],
			[ true,  'CREATE TABLE table_05 ( id INTEGER PRIMARY , name STRING , flag BOOLEAN )', 'excess internal whitespace is allowed' ],
			[ true,  'CREATE TABLE table_06 (id INTEGER PRIMARY,name STRING,flag BOOLEAN)', 'whitespace around punctuation is not required' ],
			[ false, 'CREATETABLE table_07 (id INTEGERPRIMARY, name STRING, flag BOOLEAN)', 'whitespace between keywords is required ' ],
			[ false, 'CREATE TABLEtable_08 (idINTEGER PRIMARY, nameSTRING, flag BOOLEAN)', 'whitespace between keywords and names is required' ],
			
			// CREATE TABLE, SYNTAX: NAMES AND KEYWORDS
			[ true,  'CREATE TABLE t (i INTEGER PRIMARY, n STRING, f BOOLEAN)', 'names can be a single letter' ],
			[ false, 'CREATE TABLE 1table_10 (2id INTEGER PRIMARY, 3name STRING, 4flag BOOLEAN)', 'a name cannot start with a number' ],
			[ false, 'CREATE TABLE _table_11 (_id INTEGER PRIMARY, _name STRING, _flag BOOLEAN)', 'a name cannot start with an underscore' ],
			[ false, 'CREATE TABLE (id INTEGER PRIMARY, name STRING, flag BOOLEAN)', 'the table name cannot be omitted' ],
			[ false, 'CREATE table_13 (id INTEGER PRIMARY, name STRING, flag BOOLEAN)', 'the TABLE keyword is required' ],
			
			// CREATE TABLE, SYNTAX: PUNCTUATION AND QUANTITY
			[ false, 'CREATE TABLE table_14 (id INTEGER PRIMARY name STRING flag BOOLEAN)', 'the commas between definitions are required' ],
			[ false, 'CREATE TABLE table_15 id INTEGER PRIMARY, name STRING, flag BOOLEAN', 'the parentheses are required' ],
			[ false, 'CREATE TABLE table_16 ()', 'there must be at least one column' ],
			[ true,  'CREATE TABLE table_17 (id INTEGER PRIMARY)', 'a single column is allowed' ],
			
			// CREATE TABLE, SEMANTICS
			[ false, 'CREATE TABLE table_18 (id INTEGER, name STRING, flag BOOLEAN)', 'there must be a primary column' ],
			[ false, 'CREATE TABLE table_19 (id INTEGER PRIMARY, name STRING PRIMARY, flag BOOLEAN PRIMARY)', 'there can be only one primary column' ],
			[ true,  'CREATE TABLE table_20 (id INTEGER, name STRING PRIMARY, flag BOOLEAN)', 'the primary column need not be the first' ],
			[ false, 'CREATE TABLE table_01 (ps STRING PRIMARY)', 'the table name must not already be in use' ],
			
			// DROP TABLE, GENERAL
			[ true,  'DROP TABLE table_01', null ],
			
			// DROP TABLE, SYNTAX: CASE AND WHITESPACE
			[ true,  'drop table table_02', 'lowercase keywords and uppercase table names are allowed' ],
			[ true,  ' DROP TABLE table_03 ', 'untrimmed whitespace is allowed' ],
			[ true,  'DROP  TABLE  table_04', 'excess internal whitespace is allowed' ],
			[ false, 'DROPTABLE table_05', 'whitespace between keywords is required ' ],
			[ false, 'DROP TABLEtable_06', 'whitespace between keywords and names is required' ],
			
			// DROP TABLE, SYNTAX: NAMES AND KEYWORDS
			[ true,  'DROP TABLE t', 'names can be a single letter' ],
			[ false, 'DROP TABLE', 'the table name cannot be omitted' ],
			[ false, 'DROP table_17', 'the TABLE keyword is required' ],
			
			// DROP TABLE AND CREATE TABLE, SEMANTICS
			[ false, 'DROP TABLE table_01', 'the table must already exist' ],
			[ true,  'CREATE TABLE table_01 (ps STRING PRIMARY)', 'previously dropped table name can be reused' ],
			
			// SHOW TABLES, GENERAL
			[ true,  'SHOW TABLES', null ],
			
			// SHOW TABLES, SYNTAX
			[ true,  'show tables', 'lowercase keywords are allowed' ],
			[ true,  ' SHOW TABLES ', 'untrimmed whitespace is allowed' ],
			[ true,  'SHOW  TABLES', 'excess internal whitespace is allowed' ],
			[ false, 'SHOWTABLES', 'whitespace between keywords is required ' ],
			[ false, 'SHOW', 'the TABLES keyword is required' ],
			
			// UNRECOGNIZED AND SCRIPTS, GENERAL
			[ false, 'AN INVALID QUERY', 'an unrecognized query should be rejected' ],
			[ true,  'ECHO "Test1"; RANGE 10; ECHO "Test2"', 'multiple semicolon-delimited queries are allowed' ],
		]
		
		computed_schemas = [
			['column_types':['integer', 'string', 'boolean'], 'column_names':['id', 'name', 'flag'], 'primary_column':0, 'table_name':'table_01'],
			['column_types':['integer', 'string', 'boolean'], 'column_names':['ID', 'NAME', 'flag'], 'primary_column':0, 'table_name':'table_02'],
			['column_types':['integer', 'string', 'boolean'], 'column_names':['id', 'name', 'flag'], 'primary_column':0, 'table_name':'table_03'],
			['column_types':['integer', 'string', 'boolean'], 'column_names':['id', 'name', 'flag'], 'primary_column':0, 'table_name':'table_04'],
			['column_types':['integer', 'string', 'boolean'], 'column_names':['id', 'name', 'flag'], 'primary_column':0, 'table_name':'table_05'],
			['column_types':['integer', 'string', 'boolean'], 'column_names':['id', 'name', 'flag'], 'primary_column':0, 'table_name':'table_06'],
			null,
			null,
			['column_types':['integer', 'string', 'boolean'], 'column_names':['i', 'n', 'f'], 'primary_column':0, 'table_name':'t'],
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			['column_types':['integer'], 'column_names':['id'], 'primary_column':0, 'table_name':'table_17'],
			null,
			null,
			['column_types':['integer', 'string', 'boolean'], 'column_names':['id', 'name', 'flag'], 'primary_column':1, 'table_name':'table_20'],
			null,
			['column_types':['integer', 'string', 'boolean'], 'column_names':['id', 'name', 'flag'], 'primary_column':0, 'table_name':'table_01'],
			['column_types':['integer', 'string', 'boolean'], 'column_names':['ID', 'NAME', 'flag'], 'primary_column':0, 'table_name':'table_02'],
			['column_types':['integer', 'string', 'boolean'], 'column_names':['id', 'name', 'flag'], 'primary_column':0, 'table_name':'table_03'],
			['column_types':['integer', 'string', 'boolean'], 'column_names':['id', 'name', 'flag'], 'primary_column':0, 'table_name':'table_04'],
			null,
			null,
			['column_types':['integer', 'string', 'boolean'], 'column_names':['i', 'n', 'f'], 'primary_column':0, 'table_name':'t'],
			null,
			null,
			null,
			['column_types':['string'], 'column_names':['ps'], 'primary_column':0, 'table_name':'table_01'],
			['column_types':['string', 'integer'], 'column_names':['table_name', 'row_count'], 'primary_column':0, 'table_name':null],
			['column_types':['string', 'integer'], 'column_names':['table_name', 'row_count'], 'primary_column':0, 'table_name':null],
			['column_types':['string', 'integer'], 'column_names':['table_name', 'row_count'], 'primary_column':0, 'table_name':null],
			['column_types':['string', 'integer'], 'column_names':['table_name', 'row_count'], 'primary_column':0, 'table_name':null],
			null,
			null,
			null,
			null,
		]
	
		computed_states = [
			[:],
			[:],
			[:],
			[:],
			[:],
			[:],
			null,
			null,
			[:],
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			[:],
			null,
			null,
			[:],
			null,
			[:],
			[:],
			[:],
			[:],
			null,
			null,
			[:],
			null,
			null,
			null,
			[:],
			['table_01':['table_01', 0], 'table_05':['table_05', 0], 'table_06':['table_06', 0], 'table_17':['table_17', 0], 'table_20':['table_20', 0]],
			['table_01':['table_01', 0], 'table_05':['table_05', 0], 'table_06':['table_06', 0], 'table_17':['table_17', 0], 'table_20':['table_20', 0]],
			['table_01':['table_01', 0], 'table_05':['table_05', 0], 'table_06':['table_06', 0], 'table_17':['table_17', 0], 'table_20':['table_20', 0]],
			['table_01':['table_01', 0], 'table_05':['table_05', 0], 'table_06':['table_06', 0], 'table_17':['table_17', 0], 'table_20':['table_20', 0]],
			null,
			null,
			null,
			null,
		]
	}
}