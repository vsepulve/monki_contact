#include <mysql.h>
#include <stdio.h>
#include <stdlib.h>
main() {
   MYSQL *conn;
   MYSQL_RES *res;
   MYSQL_ROW row;
   const char *server = "localhost";
   const char *user = "root";
   const char *password = "toor"; /* set me first */
   const char *database = "monkeycontact";
   conn = mysql_init(NULL);
   /* Connect to database */
   if (!mysql_real_connect(conn, server,
         user, password, database, 0, NULL, 0)) {
      fprintf(stderr, "no me conecto :( %s\n", mysql_error(conn));
      exit(1);
   }
   /* send SQL query */
   if (mysql_query(conn, "select address, contactId from email where contactId < 100; ")) {
      fprintf(stderr, "%s\n", mysql_error(conn));
      exit(1);
   }
   res = mysql_use_result(conn);
   /* output table name */
  // printf("MySQL Tables in mysql database:\n");
   while ((row = mysql_fetch_row(res)) != NULL){  	
      printf("%s %s \n", row[0], row[1]);
     break;
      }
   /* close connection */
   mysql_free_result(res);
   mysql_close(conn);
}
