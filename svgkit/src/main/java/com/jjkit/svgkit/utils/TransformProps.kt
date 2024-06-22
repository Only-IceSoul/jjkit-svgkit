package com.jjkit.svgkit.utils


 class TransformProps {
     var translationX: Float = 0f
     var translationY: Float = 0f
     var translationIsPercent: Boolean = false
     var translationStatus: Boolean = true

     var rotation: Float = 0f
     var rotationOx: Float = 0f
     var rotationOy: Float = 0f
     var rotationIsPercent: Boolean = false
     var rotationOriginStatus: Boolean = true

     var scaleX: Float = 1f
     var scaleY: Float = 1f
     var scaleOriginX: Float = 0f
     var scaleOriginY: Float = 0f
     var scaleOriginIsPercent: Boolean = false
     var scaleOriginStatus: Boolean = true


     fun inactiveStatus(){
         rotationOriginStatus = false
         scaleOriginStatus = false
         translationStatus = false
     }
     fun activeStatus(){
         rotationOriginStatus = true
         scaleOriginStatus = true
         translationStatus = true
     }
 }