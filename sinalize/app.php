<?php
require 'DB.php';
require 'Usuario.php';
require 'Ocorrencia.php';
require 'Slim/Slim/Slim.php';
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim(array('debug' => true));
$app->response()->header('Content-Type', 'application/json;charset=utf-8');

$app->get('/ocorrencia', function () {
	$ocorrenciaDAO = new Ocorrencia();
	$result = $ocorrenciaDAO->get();
	echo json_encode($result);
});

$app->post('/ocorrencia', function () {
	$request = \Slim\Slim::getInstance()->request();
	$ocorrencia = json_decode($request->getBody());
	$ocorrenciaDAO = new Ocorrencia();
	$ocorrenciaDAO->insert($ocorrencia);
	echo '{"result":"ok"}';
});

$app->put('/ocorrencia', function () {
	$request = \Slim\Slim::getInstance()->request();
	$ocorrencia = json_decode($request->getBody());
	$ocorrenciaDAO = new Ocorrencia();
	$ocorrenciaDAO->update($ocorrencia);
	echo '{"result":"ok"}';
});

$app->delete('/ocorrencia', function () {
	$request = \Slim\Slim::getInstance()->request();
	$ocorrencia = json_decode($request->getBody());
	$ocorrenciaDAO = new Ocorrencia();
	$ocorrenciaDAO->delete($ocorrencia);
	echo '{"result":"ok"}';
});

$app->get('/usuario', function () {
	$usuarioDAO = new Usuario();
	$result = $usuarioDAO->get();
	echo json_encode($result);
});

$app->post('/usuario', function () {
	$request = \Slim\Slim::getInstance()->request();
	$usuario = json_decode($request->getBody());
	$usuarioDAO = new Usuario();
	$usuarioDAO->insert($usuario);
	echo '{"result":"ok"}';
});

$app->put('/usuario', function () {
	$request = \Slim\Slim::getInstance()->request();
	$usuario = json_decode($request->getBody());
	$usuarioDAO = new Usuario();
	$usuarioDAO->update($usuario);
	echo '{"result":"ok"}';
});

$app->delete('/usuario', function () {
	$request = \Slim\Slim::getInstance()->request();
	$usuario = json_decode($request->getBody());
	$usuarioDAO = new Usuario();
	$usuarioDAO->delete($usuario);
	echo '{"result":"ok"}';
});

$app->run();
