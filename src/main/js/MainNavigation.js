//@flow
import React from "react";
import { translate } from "react-i18next";
import { Navigation, NavLink, Section } from "@scm-manager/ui-components";
import type { ScriptCollection } from "./types";

type Props = {
  collection: ScriptCollection,
  // context props
  t: string => string
};

class MainNavigation extends React.Component<Props> {
  render() {
    const { t } = this.props;
    return (
      <Navigation>
        <Section label="Scripts">
          <NavLink
            to="/scripts/run"
            label={t("scm-script-plugin.navigation.run")}
          />
          <NavLink
            to="/scripts"
            label={t("scm-script-plugin.navigation.stored")}
          />
          <NavLink
            to="/scripts/samples"
            label={t("scm-script-plugin.navigation.samples")}
          />
        </Section>
      </Navigation>
    );
  }
}

export default translate("plugins")(MainNavigation);
