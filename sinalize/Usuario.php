<?php
include_once 'dbconfig.php';

class Usuario{
    private $db;

    function __construct($DB_con){
        $this->db = $DB_con;
    }
    function get(){
		$stmt = DB::getConn()->query("SELECT `idUsr`,`nome`,`email`,`senha`,`pathfoto`,`pontuacao`,`nivel` FROM usuario");
		$usuarios = $stmt->fetchAll(PDO::FETCH_OBJ);
		return $usuarios;
	}

	function insert($nome,$email,$senha){
	    try{
            $query = "INSERT INTO usuario (`nome`,`email`,`senha`,`nivel`) VALUES (:nome,:email,:senha,:nivel)";
            $stmt = $this->db->prepare($query);
            $stmt->execute(array(':nome' => $nome,
                ':email' => $email,
                ':senha' => $senha,
                ':nivel' => "1" ));
            return "OK";
        }catch (PDOException $e){
            echo $e->getMessage();
            return false;
        }
	}
	function update($usuario){
		$conn = DB::getConn();
		$stmt = $conn->prepare("UPDATE usuario SET nome = :nome,senha = :senha,pathFoto = :pathfoto,pontuacao = :pontuacao WHERE idUsr = :idusr");
		$stmt->bindParam("idusr",$usuario->idusr);
		$stmt->bindParam("nome",$usuario->nome);
		$stmt->bindParam("senha",$usuario->senha);
		$stmt->bindParam("pathfoto",$usuario->pathfoto);
		$stmt->bindParam("pontuacao",$usuario->pontuacao);
		$stmt->execute();
	}
	function delete($usuario){
		$conn = DB::getConn();
		$stmt = $conn->prepare("DELETE FROM usuario WHERE idUsr = :idusr");
		$stmt->bindParam("idusr",$usuario->idusr);
		$stmt->execute();
	}
    //Login do Usuario
    function login ($email, $senha){
        try {
            $query = "SELECT idUsr, nome FROM usuario WHERE email = :email AND senha = :senha LIMIT 1";
            $stmt = $this->db->prepare($query);
            $stmt->execute(array(":email" => $email, ":senha" => $senha));
            $row = $stmt->fetch(PDO::FETCH_ASSOC);
            if ($stmt->rowCount() > 0) {
                $result = $row['idUsr'];
                return $result;
            }else{
                return false;
            }
        } catch (PDOException $e) {
            echo $e->getMessage();
            return false;
        }
    }


}