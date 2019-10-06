______________________________________________________________________________________________________________

IMAGE IS:  springio/photoprocessing:latest


mvn install dockerfile:build

docker run -p 8080:8080 -t springio/photoprocessing
______________________________________________________________________________________________________________

      ***********
>>>>> FOR SWAGGER <<<<< 
      ***********
	  
http://localhost:8080/swagger-ui.html
______________________________________________________________________________________________________________

*********************
Shows mounted systems:
*********************
cat /etc/mtab

***************************
Unmount all mounted systems:
***************************
sudo umount -a

************************************************************************************
For mounting PT code as standard/first (non-root) user in VirtualBox guest Ubuntu OS 
(considering associated PANORAMIC_TREKKING shared setup in VB Sharing)
************************************************************************************
sudo mount -o uid=1000,gid=1000 -t vboxsf PANORAMIC_TREKKING Mounted_PanoramicTrekking_Code
______________________________________________________________________________________________________________
