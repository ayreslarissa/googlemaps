<?php
/**
 * Created by PhpStorm.
 * User: Ervili Tarsila
 * Date: 17/11/2015
 * Time: 15:43
 */
require 'dbconfig.php';
require 'Usuario.php';
require 'Ocorrencia.php';
require 'Comentario.php';


$usuario = new Usuario($DB_con);
$ocorrencia = new Ocorrencia($DB_con);
$comentario = new Comentario($DB_con);


if(isset($_POST['method'])){

     /* USUARIO */

     // Cadastrar Usuario
    if(strcmp('cadastroUsuario', $_POST['method']) == 0) {
        List($nome,$email,$senha) =  explode(", ",$_POST['data']);
        $result = $usuario->insert($nome,$email,$senha);
        echo json_encode(array('mensagem'=>$result));
    }
    //Login
    if(strcmp('login', $_POST['method']) == 0) {
        List($email,$senha) =  explode(", ",$_POST['data']);
        $result = $usuario->login($email,$senha);
        //echo json_encode(array('mensagem'=>$result));
        echo $result;
    }

    /* OCORRENCIA */

    //Cadastrar Ocorrencia
    if(strcmp('sinalizarOcorrencia', $_POST['method']) == 0) {
        $status = 1; // Status de ocorrencia aberta

        //List($latitude, $londitude, $ruaend, $bairroend,  $observacao, $datasinalizacao,  $usrsinalizacao, $tipoocorrencia) =  explode(", ",$_POST['data']);
       // List($latitude, $londitude,  $observacao, $usrsinalizacao, $tipoocorrencia) =  explode(", ",$_POST['data']);
        List($latitude, $londitude, $observacao, $usrsinalizacao, $tipoocorrencia) =  explode(", ",$_POST['data']);
        //$result = $ocorrencia->insert($latitude, $londitude, $ruaend, $bairroend, $status, $observacao, $usrsinalizacao, $tipoocorrencia);
        //$result = $ocorrencia->insert($latitude, $londitude, $status, $observacao, $usrsinalizacao, $tipoocorrencia);
        $result = $ocorrencia->insert($latitude, $londitude,  $observacao, $usrsinalizacao, $status, $tipoocorrencia);

        echo json_encode(array('mensagem'=>$result));
    }

    if(strcmp('liberarOcorrencia', $_POST['method']) == 0) {
        //$dataLiberacao =  date('Y-m-d H:i:s');
        List($idOcorrencia,$usrLiberacao) =  explode(", ",$_POST['data']);
        $result = $ocorrencia->liberarOcorrencia($idOcorrencia,$usrLiberacao);
        echo json_encode(array('mensagem'=>$result));
    }
    // Filtrar por tipo de Ocorrencia
    if(strcmp('filtrarOcorrencia', $_POST['method']) == 0) {
        $result = $ocorrencia->filtrarOcorrencia($_POST['data']);
        echo json_encode(array('mensagem'=>$result));
    }

 /* COMENTARIO */

    // Inserir um comentario de uma ocorrencia
    if(strcmp('inserirComentario', $_POST['method']) == 0) {
        List($usr,$comentarioMSG,$idOcorrencia) =  explode(", ",$_POST['data']);
        $result = $comentario->inserirComentario($usr,$comentarioMSG,$idOcorrencia);
        echo json_encode(array('mensagem'=>$result));
    }


    if(strcmp('listaComentarios', $_POST['method']) == 0) {
        $result = $comentario->listaComentarios($_POST['data']);
        echo json_encode(array('mensagem'=>$result));
    }
}