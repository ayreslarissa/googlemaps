<?php

include_once 'dbconfig.php';

// Como este php é muito utilizado, isso aqui serve para configurar a zona corretamente
date_default_timezone_set("America/Manaus");

class Ocorrencia{
    private $db;

    function __construct($DB_con){
        $this->db = $DB_con;
    }
    function get(){
		$stmt = DB::getConn()->query("SELECT `idOcorrencia`,`latitude`,`londitude`,`ruaEnd`,`bairroEnd`,`status`,`observacao`,`dataSinalizacao`,`dataLiberacao`,`usrSinalizacao`,`usrLiberacao`,`tipoOcorrencia` FROM ocorrencia");
		$ocorrencias = $stmt->fetchAll(PDO::FETCH_OBJ);
		return $ocorrencias;
	}

	function update($ocorrencia){
		$conn = DB::getConn();
		$stmt = $conn->prepare("UPDATE ocorrencia SET latitude = :latitude,londitude = :londitude,ruaEnd = :ruaend,bairroEnd = :bairroend,status = :status,observacao = :observacao,dataSinalizacao = :datasinalizacao,dataLiberacao = :dataliberacao WHERE idOcorrencia = :idocorrencia");
		$stmt->bindParam("idocorrencia",$ocorrencia->idocorrencia);
		$stmt->bindParam("latitude",$ocorrencia->latitude);
		$stmt->bindParam("londitude",$ocorrencia->londitude);
		$stmt->bindParam("ruaend",$ocorrencia->ruaend);
		$stmt->bindParam("bairroend",$ocorrencia->bairroend);
		$stmt->bindParam("status",$ocorrencia->status);
		$stmt->bindParam("observacao",$ocorrencia->observacao);
		$stmt->bindParam("datasinalizacao",$ocorrencia->datasinalizacao);
		$stmt->bindParam("dataliberacao",$ocorrencia->dataliberacao);
		$stmt->execute();
	}
	function delete($ocorrencia){
		$conn = DB::getConn();
		$stmt = $conn->prepare("DELETE FROM ocorrencia WHERE idOcorrencia = :idocorrencia");
		$stmt->bindParam("idocorrencia",$ocorrencia->idocorrencia);
		$stmt->execute();
	}



    public function reverseGeoCoding($latitude, $longitude){
        $url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=".$latitude.",".$longitude;
        //echo "URL: ".$url;

        file_put_contents("tmpfile.json", fopen($url, 'r'));
        $string = file_get_contents("tmpfile.json");
        $jsonObj = json_decode($string);
        $resultados = $jsonObj->results;
        $bairroEntrou = false;
        $bairro = "";
        $rua = "";
        foreach ($resultados as $res) {
            $address = $res->address_components;
            foreach ($address as $add) {
                $types = $add->types;
                foreach ($types as $typ) {
                    if ($typ == "route") {
                        $rua = $add->short_name;
                        //print("Rua: " . $add->short_name . "<br>");
                    }
                    if (($typ == "sublocality_level_1")&&($bairroEntrou == false)){
                        $bairroEntrou = true;
                        $bairro = $add->short_name;
                        //print("Bairro: " . $add->short_name . "<br>");
                    }
                }
            }

        }
        return array($rua,$bairro);
    }

    //function insert( $latitude, $longitude, $ruaend, $bairroend, $status, $observacao, $usrsinalizacao, $tipoocorrencia){
    function insert( $latitude, $londitude,  $observacao, $usrsinalizacao, $status, $tipoocorrencia){
        
        try{

            $datasinalizacao = date('Y-m-d H:i:s');

            $endereco = $this->reverseGeoCoding($latitude,$londitude);


            $query = "INSERT INTO ocorrencia (`latitude`,`londitude`,`ruaEnd`,`bairroEnd`,`status`,`observacao`,`dataSinalizacao`,`usrSinalizacao`,`tipoOcorrencia`)
                    VALUES
                      (:latitude,:londitude,:ruaend,:bairroend,:status,:observacao,:datasinalizacao, :usrsinalizacao,:tipoocorrencia)";
            $stmt = $this->db->prepare($query);
            $stmt->execute(array(
                ':latitude' => $latitude,
                ':londitude' => $londitude,
                ':ruaend' => utf8_encode($endereco[0]),
                ':bairroend' => utf8_encode($endereco[1]),
                ':status' => $status,
                ':observacao' => $observacao,
                ':datasinalizacao' => $datasinalizacao,
                ':usrsinalizacao' => $usrsinalizacao,
                ':tipoocorrencia' => $tipoocorrencia
            ));
            return "OK";
        }catch (PDOException $e){
            echo $e->getMessage();
            return false;
        }
    }

    function liberarOcorrencia($idOcorrencia,$usrLiberacao){
        try {
            $dataLiberacao = date('Y-m-d H:i:s');
            $query = "UPDATE ocorrencia SET usrLiberacao = :usrLiberacao,
                                            dataLiberacao = :dataLiberacao,
                                            status = :status
                                            WHERE idOcorrencia = :idOcorrencia";
            $stmt = $this->db->prepare($query);
            $stmt->execute(array(
                ':idOcorrencia' => $idOcorrencia,
                ':usrLiberacao' => $usrLiberacao,
                ':dataLiberacao' => $dataLiberacao,
                ':status' => 2
            ));
            return "OK";
        } catch (PDOException $e) {
            echo $e->getMessage();
            return false;
        }
    }

    function filtrarOcorrencia ($tipoOcorrencia){
        try {
        /*   $query = "SELECT latitude, longitude, ruaEnd, bairroEnd, dataSinalizacao, observacao
                     FROM ocorrencia
                     WHERE tipoocorrencia = :tipoOcorrencia AND status = 1";*/
            $query = "SELECT idOcorrencia
                        FROM ocorrencia
                        WHERE tipoocorrencia = :tipoOcorrencia AND status = 1";         
            $stmt = $this->db->prepare($query);
            $stmt->execute(array(':tipoOcorrencia' => $tipoOcorrencia));
            $ocorrencias = $stmt->fetchAll(PDO::FETCH_ASSOC);
            return $ocorrencias;
        } catch (PDOException $e) {
            echo $e->getMessage();
            return false;
        }
    }
}
