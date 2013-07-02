<!DOCTYPE html>
<html>
    <head>
        <title>GoFish!</title>
        <meta charset="utf-8">
        <link rel="shortcut icon" href="img/favicon.ico">
        <link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="css/gofish.css">
    </head>
    <body>
        <div id="container" class="container"></div>
        
        <script src="js/config.js"></script>
        <script src="//ajax.googleapis.com/ajax/libs/dojo/1.9.0/dojo/dojo.js"></script>
        <script>
            require(['dojo/dom', 'gofish/game', 'dojo/domReady!'], function(dom, Game) {
                // DOM is ready, create main view and run game
                var container = dom.byId('container'),
                    game = new Game(container);

                game.run();
            });
        </script>
    </body>
</html>
