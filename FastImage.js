import React, { Component } from 'react'
import PropTypes from 'prop-types'
import {
  Image,
  NativeModules,
  requireNativeComponent,
  ViewPropTypes,
  StyleSheet,
  Platform
} from 'react-native'

const resolveAssetSource = require('react-native/Libraries/Image/resolveAssetSource')

const FastImageViewNativeModule = NativeModules.FastImageView

class FastImage extends Component {
  setNativeProps(nativeProps) {
    this._root.setNativeProps(nativeProps)
  }

  render() {
    const {
      source,
      circle,
      defaultSource,
      onLoadStart,
      onProgress,
      onLoad,
      onError,
      onLoadEnd,
      style,
      children,
      onPhotoTapListener,
      zoom,
      ...props
    } = this.props

    // If there's no source or source uri just fallback to Image.
    if (!source || !source.uri) {
      return (
        <Image
          ref={e => (this._root = e)}
          {...props}
          style={style}
          source={source}
          defaultSource={defaultSource}
          onLoadStart={onLoadStart}
          onProgress={onProgress}
          onLoad={onLoad}
          onError={onError}
          onLoadEnd={onLoadEnd}
        />
      )
    }

    const resolvedSource = resolveAssetSource(source)
    const resolvedDefaultSource = resolveAssetSource(defaultSource)
    if (zoom) {
      return (
        <PhotoView
          ref={e => (this._root = e)}
          {...props}
          style={style}
          circle={circle}
          source={resolvedSource}
          defaultSource={resolvedDefaultSource}
          onFastImageLoadStart={onLoadStart}
          onFastImageProgress={onProgress}
          onFastImageLoad={onLoad}
          onFastImageError={onError}
          onFastImageLoadEnd={onLoadEnd}
          onPhotoTapListener={onPhotoTapListener}
          {...Platform.select({
            ios: {
              resizeMode: props.resizeMode ? props.resizeMode : 'cover'
            }
          })}
        />
      )
    }
    return (
      <FastImageView
        ref={e => (this._root = e)}
        {...props}
        style={style}
        circle={circle}
        source={resolvedSource}
        defaultSource={resolvedDefaultSource}
        onFastImageLoadStart={onLoadStart}
        onFastImageProgress={onProgress}
        onFastImageLoad={onLoad}
        onFastImageError={onError}
        onFastImageLoadEnd={onLoadEnd}
        {...Platform.select({
          ios: {
            resizeMode: props.resizeMode ? props.resizeMode : 'cover'
          }
        })}
      />
    )
  }
}

const styles = StyleSheet.create({
  imageContainer: {
    overflow: 'hidden'
  }
})

FastImage.resizeMode = {
  contain: 'contain',
  cover: 'cover',
  stretch: 'stretch',
  center: 'center'
}

FastImage.priority = {
  low: 'low',
  normal: 'normal',
  high: 'high'
}

FastImage.preload = sources => {
  FastImageViewNativeModule.preload(sources)
}

const FastImageSourcePropType = PropTypes.shape({
  uri: PropTypes.string,
  headers: PropTypes.objectOf(PropTypes.string),
  priority: PropTypes.oneOf(Object.keys(FastImage.priority))
})

FastImage.propTypes = {
  ...ViewPropTypes,
  source: PropTypes.oneOfType([FastImageSourcePropType, PropTypes.number]),
  defaultSource: PropTypes.oneOfType([
    FastImageSourcePropType,
    PropTypes.number
  ]),
  circle: PropTypes.bool,
  onLoadStart: PropTypes.func,
  onProgress: PropTypes.func,
  onLoad: PropTypes.func,
  onError: PropTypes.func,
  onLoadEnd: PropTypes.func
}

const FastImageView = requireNativeComponent('FastImageView', FastImage, {
  nativeOnly: {
    onFastImageLoadStart: true,
    onFastImageProgress: true,
    onFastImageLoad: true,
    onFastImageError: true,
    onFastImageLoadEnd: true
  }
})

const PhotoView = requireNativeComponent('PhotoView', PhotoView, {
  nativeOnly: {
    onPhotoTapListener: true
  }
})

export default FastImage
