<?php
/**
 * Created by PhpStorm.
 * User: Ervili Tarsila
 * Date: 10/10/2015
 * Time: 18:21
 */

    $DB_host = "localhost";
    $DB_user = "root";
    $DB_name = "sinalize";
    $DB_pass = "";
    $DB_con  = null;
    try {
        $DB_con = new PDO("mysql:host={$DB_host};dbname={$DB_name}", $DB_user, $DB_pass);
        $DB_con->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    } catch (PDOException $e) {
        echo $e->getMessage();
    }

?>
