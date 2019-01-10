//@flow
import React from "react";
import { Switch, Route } from "react-router-dom";
import { translate } from "react-i18next";
import { Page, Loading, ErrorNotification } from "@scm-manager/ui-components";
import { findAllScriptLinks } from "./api";
import type { ScriptLinks } from "./types";
import Main from "./Main";
import StoredDetails from "./StoredDetails";

type Props = {
  link: string,
  // context props
  t: string => string
};

type State = {
  loading: boolean,
  error?: Error,
  links?: ScriptLinks
};

class RootPage extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: true
    };
  }

  componentDidMount(): void {
    const { link } = this.props;
    findAllScriptLinks(link)
      .then(links => {
        this.setState({
          loading: false,
          links
        });
      })
      .catch(error => {
        this.setState({
          loading: false,
          error
        });
      });
  }

  createBody = () => {
    const { loading, error, links } = this.state;
    if (error) {
      return <ErrorNotification error={error} />;
    } else if (loading) {
      return <Loading />;
    } else {
      return (
        <Switch>
          <Route path="/scripts" component={() => <Main links={links} />} />
          <Route
            path="/script/:id"
            component={() => <StoredDetails links={links} />}
          />
        </Switch>
      );
    }
  };

  render() {
    const { t } = this.props;
    const body = this.createBody();

    return (
      <Page
        title={t("scm-script-plugin.root-page.title")}
        subtitle={t("scm-script-plugin.root-page.subtitle")}
      >
        {body}
      </Page>
    );
  }
}

export default translate("plugins")(RootPage);