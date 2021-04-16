import logo from "./logo.svg";
import "./App.css";
import HomeScreen from "./screens/HomeScreen";
import Layout from "./components/Layout/Layout";
import { Route, BrowserRouter } from "react-router-dom";

function App() {
  return (
    <div className="App">
      <BrowserRouter>
        <Layout />
        <Route exact path="/" component={HomeScreen} />
      </BrowserRouter>
    </div>
  );
}

export default App;
