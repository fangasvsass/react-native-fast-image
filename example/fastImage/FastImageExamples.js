import React from 'react'
import { ScrollView, StatusBar, StyleSheet, Text, View } from 'react-native'
import Icon from 'react-native-vector-icons/Ionicons'
import Section from './Section'
import PriorityExample from './PriorityExample'
import GifExample from './GifExample'
import BorderRadiusExample from './BorderRadiusExample'
import FeatureText from './FeatureText'
import ProgressExample from './ProgressExample'
import PreloadExample from './PreloadExample'
import FastImage from 'react-native-letote-fast-image'
import StatusBarUnderlay, { STATUS_BAR_HEIGHT } from './StatusBarUnderlay'

const FastImageExample = () => (
  <View style={styles.container}>
    <StatusBar
      translucent
      barStyle="dark-content"
      backgroundColor="transparent"
    />

    <ScrollView
      style={styles.scrollContainer}
      contentContainerStyle={styles.scrollContentContainer}
    >
      <View style={styles.contentContainer}>
        <Section>
          <Text style={styles.titleText}>🚩 FastImage</Text>
          <FeatureText text="Tap images to reload examples." />
        </Section>
        <PriorityExample />
        <GifExample />
        <BorderRadiusExample />
        <ProgressExample />
        <PreloadExample />
      </View>
    </ScrollView>
    <StatusBarUnderlay />
  </View>
)

FastImageExample.navigationOptions = {
  tabBarLabel: 'FastImage Example',
  tabBarIcon: ({ focused, tintColor }) => {
    const name = focused
      ? 'ios-information-circle'
      : 'ios-information-circle-outline'
    return <Icon name={name} size={26} color={tintColor} />
  }
}

const styles = StyleSheet.create({
  titleText: {
    fontWeight: '900',
    marginBottom: 20,
    color: '#222'
  },
  contentContainer: {
    marginTop: 20,
    marginBottom: 20
  },
  image: {
    flex: 1,
    height: 100,
    backgroundColor: '#ddd',
    margin: 10,
    borderRadius: 10
  },
  image1: {
    margin: 20,
    height: 300,
    width: 200,
    borderRadius: 10
  },
  container: {
    flex: 1,
    alignItems: 'stretch',
    backgroundColor: '#fff'
  },
  scrollContainer: {
    marginTop: STATUS_BAR_HEIGHT
  },
  scrollContentContainer: {
    alignItems: 'stretch',
    flex: 0
  }
})

export default FastImageExample
