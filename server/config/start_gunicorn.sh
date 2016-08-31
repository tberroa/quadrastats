APPNAME=portal
BACKEND=$HOME/$APPNAME/backend
APPDIR=$BACKEND/$APPNAME

LOGFILE=$HOME/logs/gunicorn/'gunicorn.log'

NUM_WORKERS=5

ADDRESS=127.0.0.1:8000

cd $APPDIR

source $BACKEND/myvenv/bin/activate

exec gunicorn $APPNAME.wsgi:application \
-w $NUM_WORKERS \
--bind=$ADDRESS \
--log-level=debug \
--log-file=$LOGFILE &