import React, { useEffect } from "react";
import "./Layout.css";
import Drawer from "@material-ui/core/Drawer";
import AppBar from "@material-ui/core/AppBar";
import IconButton from "@material-ui/core/IconButton";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import MenuIcon from "@material-ui/icons/Menu";
import Button from "@material-ui/core/Button";
import List from "@material-ui/core/List";
import {Link} from "react-router-dom";
import ListItem from "@material-ui/core/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText";
import MapIcon from '@material-ui/icons/Map';
import HomeIcon from '@material-ui/icons/Home';
import PlaceIcon from '@material-ui/icons/Place';

const Layout = () => {
  const [drawerOpen, setDrawerOpen] = React.useState(false);

  function toggleDrawer() {
    setDrawerOpen(!drawerOpen);
  }

  const handleLogout = () => {
    localStorage.clear();
    window.location.reload();
  };

  const list =()=>{
    return (
        <List style={{ backgroundColor: "#f2f2f2" }}>
          <Link
              to={"/option0"}
              style={{ textDecoration: "none" }}
          >
            <ListItem button onClick={() => toggleDrawer()}>
              <ListItemIcon>
                <PlaceIcon />
              </ListItemIcon>
              <ListItemText primary={"Go to option 0"} />
            </ListItem>
          </Link>
          <Link
              to={"/option1"}
              style={{ textDecoration: "none" }}
          >
            <ListItem button onClick={() => toggleDrawer()}>
              <ListItemIcon>
                <MapIcon />
              </ListItemIcon>
              <ListItemText primary={"Go to option 1"} />
            </ListItem>
          </Link>
          <Link
              to={"/option2"}
              style={{ textDecoration: "none" }}
          >
            <ListItem button onClick={() => toggleDrawer()}>
              <ListItemIcon>
                <HomeIcon />
              </ListItemIcon>
              <ListItemText primary={"Go to option 2"} />
            </ListItem>
          </Link>

          



        </List>
    );
  }
    return (
    <div>
      <AppBar position="sticky" style={{background: '#008080'}}>
        <Toolbar>
          <IconButton
            edge="start"
            color="inherit"
            aria-label="menu"
            onClick={() => toggleDrawer()}
          >
            <MenuIcon />
          </IconButton>
          <Typography style={{ flexGrow: 1 }} variant="h5">
            CMS-UI
          </Typography>
          <Button onClick={handleLogout} color="inherit">
            LOGOUT
          </Button>
        </Toolbar>
      </AppBar>

      <Drawer anchor={"left"} open={drawerOpen} onClose={() => toggleDrawer()}>
        {list()}
      </Drawer>
    </div>
  );
};

export default Layout;