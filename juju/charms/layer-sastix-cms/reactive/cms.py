# pylint: disable=unused-argument
from subprocess import Popen
from charmhelpers.core.hookenv import open_port, log
from charms.reactive import when, when_not, set_state, remove_state
from charmhelpers.core.hookenv import status_set


@when_not('mysql.available')
def waiting_for_db():
    # TODO: stop service
    status_set('blocked', 'waiting for mysql relation')
    remove_state('cms.started')


@when('mysql.available')
@when_not('cms.started')
def setup(mysql):
    status_set('maintenance', 'installing Sastix CMS')
    connectionstr="jdbc:mysql://{}:{}/{}".format(mysql.host(), mysql.port(), mysql.database())
    log("Connection string: {}".format(connectionstr))
    log("Connection user/pass: {}/{}".format(mysql.user(), mysql.password()))
    cmd = ['java', '-jar', 'resources/jars/cms-server-0.0.1-SNAPSHOT.jar',
          '--spring.datasource.url={}'.format(connectionstr),
          '--spring.datasource.username={}'.format(mysql.user()),
          '--spring.datasource.password={}'.format(mysql.password()),
          '--security.basic.enabled=false']
    Popen(cmd)
    open_port(9082)
    status_set('active', 'ready')
    set_state('cms.started')
