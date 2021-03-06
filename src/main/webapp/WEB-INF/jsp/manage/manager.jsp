<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>  
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
	<title>Chess AR Online Server Program Manager</title>
	
	<style>
		body {
			width: calc(100% - 16px);
		}
		
		#statusPanel {
			height: 5%;
			border-bottom: 1px solid gray;
		}
		
		#statusPanel ul {
			list-style: none;
			display: inline-block;	
		}
		
		.btns {
			float: right;
			margin-right: 10%;
		}
		
		#statusPanel ul > li {
			display: inline;
		}
		
		#online {
			color: green;
		}
		
		#offline {
			color: red;
		}
		
		.on {
			background-color: green;
			color: green;
		}
		
		.off {
			background-color: red;
			color: red;
		}
		
		#list {
			float: left;
			width: 48%;
		}
		
		#inputPanel {
			float: right;
			width: 48%;
		}
		
		#inputPanel > * {
			margin: 5%;
			
		}
		
		#newGameDiv fieldset {
			margin-bottom: 5%;
		}
		
		#enqueueDiv fieldset {
			margin-bottom: 5%;
		}
		
		#addForm {
			float: right;
		}
		
		#gameList {
			border: 1px black solid;
			list-style: none;
			width: 100%;
			height: 200px;
			overflow-y: scroll;
		}
		
		#inQueue {
			border: 1px black solid;
			list-style: none;
			width: 100%;
			height: 200px;
			overflow-y: scroll;
		}
		
		.hide {
			display: none;
		}
		
		
	</style>
	
	<script src="http://code.jquery.com/jquery-2.2.1.min.js" type="text/javascript"></script>
	<script>
		var timer = null;
		
		$(document).ready(function() {
			timer = setInterval(refresh, 5000);
			$('.game').bind('click', gameHandler);
			
			$('#exitGame').bind('click', function() {
				var sessionKey = $('#gameInfo').attr('data');
				$.ajax({method: "post", url: 'closeSession.cao', data: {sessionKey: sessionKey}}).done(function(data) {
					var json = JSON.parse(data);
					
					for (var i = 0; i < $('.game').length; ++i) {
						if ($('.game:eq('+ i +')').text() == json['sessionKey']) {
							$('.game:eq('+ i +')').remove();
						}
					}
					
					$('#gameInfo').text('');
					if (!$('#exitGame').hasClass('hide')) {
						$('#exitGame').addClass('hide');
					}
				});
			});
			
			$('#addForm').bind('click', function() {
				$('#enqueueForm').prepend('<fieldset><legend>Enqueue User</legend><input type=\"text\" placeholder=\"nink\" id=\"nick\"><input type=\"text\" placeholder=\"gcmToken\" id=\"token\">');
				return false;
			});
			
			$('#run').bind('click', function() {
				toggleInputPanel($('#newGameDiv'));
			});
			
			$('#enqueue').bind('click', enqueueHandler);
			
			$('#gameCreateSubmit').bind('click', function() {
				$.ajax({
					method: 'post',
					url: 'createGame.cao', 
					data: {
						wnick: $('#wnick').val(),
						wgcmToken: $('#wtoken').val(),
						bnick: $('#bnick').val(),
						bgcmToken: $('#btoken').val()}
					}
				).success(
					function(data) {
						alert(data)
						refresh();
					}
				);
				
				return false;
			});
			
			$('#enqueueSubmit').bind('click', function() {
				$.ajax({
					method: 'post',
					url: 'enqueueUser.cao', 
					data: {
						nick: $('#nick').val(),
						gcmToken: $('#token').val()
					}
				}).success(
					function() {
						refresh();
					}
				);
				
				return false;
			});
			
			$('#refreshBtns .on > button').bind('click', function() {
				timer = setInterval(refresh, 5000);
				$('#refreshBtns .on > button').prop('disabled', true);
				$('#refreshBtns .off > button').prop('disabled', false);
			});
			
			$('#refreshBtns .off > button').bind('click', function() {
				clearInterval(timer);
				$('#refreshBtns .off > button').prop('disabled', true);
				$('#refreshBtns .on > button').prop('disabled', false);
			});
			
			$('#mmBtns .on > button').bind('click', function() {
				$.ajax({url: "onMM.cao"});
				location.reload();
				$('#refreshBtns .off > button').prop('disabled', true);
				$('#refreshBtns .on > button').prop('disabled', false);
			});
			
			$('#mmBtns .off > button').bind('click', function() {
				$.ajax({url: "offMM.cao"});
				location.reload();
				$('#refreshBtns .off > button').prop('disabled', true);
				$('#refreshBtns .on > button').prop('disabled', false);
			});
			
			$('.turnOverBtn').bind('click', function() {
				var sessionKey = $('#gameInfo').attr('data');
				var player = $(this).attr('data');
				$.ajax({
					method: 'post',
					url: 'turnOver.cao', 
					data: {
						sessionKey: sessionKey,
						player: player
						}
				}).success(function(data) {
					var response = JSON.parse(data);
					
					if (response['status'] == 'SUCCESS') {
						if (response['nowTurn'] == 'white') {
							$('.turnOverBtn[data="white"]').prop('disabled', false);
							$('.turnOverBtn[data="black"]').prop('disabled', true);
						}
						else {
							$('.turnOverBtn[data="white"]').prop('disabled', true);
							$('.turnOverBtn[data="black"]').prop('disabled', false);
						}
					}
					else {
						alert("error!");
					}
					
					location.reload();
				});
			});
			
		})
		
		var gameHandler = function() {
			var sessionKey = $(this).text();
			$('#gameInfo').attr('data', sessionKey);
			
			toggleInputPanel($('#gameInfo'));
			
			$.ajax({method: "post", url: 'getGameInfo.cao', data: {sessionKey: sessionKey}}).done(function(data) {
				//$('#gameInfo').text(data);
				var json = JSON.parse(data);
				var white = json['white'];
				var black = json['black'];
				var turn = json['turn'];
				
				$('#whiteField > p:eq(0)').text(white['nick'] + '(' + white['id'] + ')');
				$('#whiteField > p:eq(1) > span').text(white['phase']);
				$('#blackField > p:eq(0)').text(black['nick'] + '(' + black['id'] + ')');
				$('#blackField > p:eq(1) > span').text(black['phase']);
				
				if (turn == "white") {
					$('#whiteField > button').prop('disabled', false);
				}
				else {
					$('#blackField > button').prop('disabled', false);
				}
			});
			
			if ($('#exitGame').hasClass('hide')) {
				$('#exitGame').removeClass('hide');
			}
		}
		
		var enqueueHandler = function() {
			toggleInputPanel($('#enqueueDiv'));
		}
		

		var refresh = function() {
			$.ajax({url: 'refreshAll.cao'}).success(function(data) {
				var json = JSON.parse(data);
				
				$('#gameList').empty();
				$('#inQueue').empty();
				for (var i = 0; i < json.gameList.length; ++i) {
					$('#gameList').append('<li class="game">' + json.gameList[i]+ '</li>');
				}
				
				for (var i = 0; i < json.queue.length; ++i) {
					$('#inQueue').append('<li class="player">' + json.queue[i] + '</li>');
				}
				
				$('.game').bind('click', gameHandler);
			})
		}
		
		var toggleInputPanel = function(target) {
			$('#inputPanel > div').addClass('hide');
			target.removeClass('hide');
		}
	</script>
</head>
<body>
	<div id="statusPanel">
		<ul>
			<li>MatchMaker: 
			<c:choose>
				<c:when test="${mmIsRunning == true }"><span id="online">Online</span></c:when>
				<c:otherwise><span id="offline">Offline</span></c:otherwise>
			</c:choose>
			</li>
			<li class="seperate"></li>
			
		</ul>
		<ul class="btns" id="refreshBtns">
			<li>Refresh</li>
			<li class="on"><button disabled>On</button></li>
			<li class="off"><button>Off</button></li>
		</ul>
		<ul class="btns" id="mmBtns">
			<li>MatchMaker</li>
			<c:choose>
				<c:when test="${mmIsRunning == true }">
				<li class="on"><button disabled>On</button></li>
				<li class="off"><button>Off</button></li>
				</c:when>
				<c:otherwise>
				<li class="on"><button>On</button></li>
				<li class="off"><button disabled>Off</button></li>
				</c:otherwise>
			</c:choose>
		</ul>
	</div>
	<div id="list">
		<ul id="gameList">
		<c:forEach var="game" items="${gameList}">
			<li class="game"><c:out value="${game}" /></li>
		</c:forEach>
		</ul>
		
		<button id="exitGame" class="hide">Exit Game</button>
		<button id="run">Run New Game</button>
		
		<ul id="inQueue">
		<c:forEach var="player" items="${queue }">
			<li class="player"><c:out value="${player.getNickName()}" />(<c:out value="${player.getId()}" />)</li>
		</c:forEach>
		</ul>
		
		<button id="enqueue">Enqueue User</button>
	</div>
	<div id="inputPanel">
		<div id="newGameDiv" class="hide">
			<form id="newGameForm">
				<fieldset>
					<legend>White Player</legend>
					<input type="text" placeholder="nink" id="wnick">
					<input type="text" placeholder="gcmToken" id="wtoken">
				</fieldset>
				<fieldset>
					<legend>Black Player</legend>
					<input type="text" placeholder="nink" id="bnick">
					<input type="text" placeholder="gcmToken" id="btoken">
				</fieldset>
				<input type="submit" value="Create New Game!" id="gameCreateSubmit">
			</form>
		</div>
		<div id="enqueueDiv" class="hide">
			<form id="enqueueForm">
				<fieldset>
					<legend>Enqueue User</legend>
					<input type="text" placeholder="nink" id="nick">
					<input type="text" placeholder="gcmToken" id="token">
					<!--<button id="addForm">+</button>-->
				</fieldset>
				<input type="submit" value="Enqueue!" id="enqueueSubmit">
			</form>
		</div>
		<div id="gameInfo" class="hide">
			<fieldset id="whiteField">
				<legend>White Player</legend>
				<p>NickName(IDIDIDIDIDIDIDIDIDIDIDIDIDI)</p>
				<p>Phase: <span></span></p>
				<button class="turnOverBtn" data="white" disabled>Turn Over</button>
			</fieldset>
			<span>VS</span>
			<fieldset id="blackField">
				<legend>Black Player</legend>
				<p>NickName(IDIDIDIDIDIDIDIDIDIDIDIDIDI)</p>
				<p>Phase: <span></span></p>
				<button class="turnOverBtn" data="black" disabled>Turn Over</button>
			</fieldset>
		</div>
	</div>	
</body>
</html>