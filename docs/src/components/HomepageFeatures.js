import React from 'react';
import clsx from 'clsx';
import styles from './HomepageFeatures.module.css';

const FeatureList = [
  {
    title: 'Fully Open Source',
    Svg: require('../../static/img/1283789705.svg').default,
    description: (
      <>
        Sastix CMS is a fully open source project, provided free for all.
      </>
    ),
  },
  {
    title: 'Configurable',
    Svg: require('../../static/img/kcontrol.svg').default,
    description: (
      <>
        Customizable to fit your needs. Configure the deployment, edit the
        settings, match to your brand.
      </>
    ),
  },
  {
    title: 'Robust and scalable',
    Svg: require('../../static/img/Procman.svg').default,
    description: (
      <>
        Leveraging the best from the most stable technologies, Sastix CMS is
        scaled and monitored with ease.
      </>
    ),
  },
];

function Feature({Svg, title, description}) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} alt={title} />
      </div>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
