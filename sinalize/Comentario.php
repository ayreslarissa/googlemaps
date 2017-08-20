<?php

include_once 'dbconfig.php';
/**
 * Created by PhpStorm.
 * User: Ervili Tarsila
 * Date: 25/01/2016
 * Time: 19:03
 */

class Comentario {
    private $db;

    function __construct($DB_con){
        $this->db = $DB_con;
    }

    function inserirComentario($usr,$comentario,$idOcorrencia)
    {
        try {
            $dataComentario = date('Y-m-d H:i:s');

            $query = "INSERT INTO comentario (`usr`,`comentario`,`idOcorrencia`,`dataComentario`) VALUES (:usr,:comentario,:idOcorrencia,:dataComentario)";
            $stmt = $this->db->prepare($query);
            $stmt->execute(array(':usr' => $usr,
                ':comentario' => $comentario,
                ':idOcorrencia' => $idOcorrencia,
                ':dataComentario'=> $dataComentario));
            return "OK";
        } catch (PDOException $e) {
            echo $e->getMessage();
            return false;
        }
    }

    function listaComentarios($idOcorrencia){
        try {
            $query = "SELECT usr, comentario, idOcorrencia, idComentario, dataComentario
                     FROM comentario
                     WHERE idOcorrencia = :idOcorrencia";
            $stmt = $this->db->prepare($query);
            $stmt->execute(array(':idOcorrencia' => $idOcorrencia));
            $comentarios = $stmt->fetchAll(PDO::FETCH_ASSOC);
            return $comentarios;
        } catch (PDOException $e) {
            echo $e->getMessage();
            return false;
        }
    }
}