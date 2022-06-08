package common.neighbour.nearhud.repositories.constance

import common.neighbour.nearhud.core.extensions.IMAGE_POST
import java.io.File

class AppConstance {
    companion object {
        @JvmStatic
        val  APP_NAME="NEARHUD"


        var mediaFile: File? = null
        var postType = IMAGE_POST

        //Chat data
        @JvmStatic
        val CHAT_SINGLE = 2000
        const val CHAT_MESSAGE = 2001
        const val CHAT_IMAGE = 2002
        const val CHAT_VIDEO = 2003
        const val CHAT_NEWSFEED = 2004
        const val CHAT_PROFILE = 2005
        const val CHAT_GROUP = 2006
        const val CHAT_GROUP_PHOTO_CHANGED = 2007
        const val CHAT_GROUP_NAME_CHANGED = 2008
        const val CHAT_GROUP_DETAIL_CHANGED = 2009
        const val CHAT_GROUP_ADMIN_CHANGED = 2010
        const val CHAT_GROUP_CREATED = 2011
        const val CHAT_GROUP_LEAVE_MEMBER = 2012
        const val CHAT_GROUP_REMOVE_MEMBER = 2013
        const val CHAT_GROUP_NEW_MEMBER = 2014
        const val CHAT_GROUP_DELETED = 2015
        const val SLIDE_IN_RIGHT:Int = 0
        const val SLIDE_IN_BOTTOM:Int = 1
        const val SLIDE_IN_LEFT:Int = 2
        const val SCALE:Int = 3

        const val RC_PLACE = 101
        const val SELECTED_PLACE_NAME = "SELECTED_PLACE_NAME"
        const val SELECTED_PLACE_ID = "SELECTED_PLACE_ID"

        const val RC_PROFILE = 102
        const val RC_UPDATE_POST = 103
        var profileMediaFile: File? = null

        /*---Api---*/
        const val BASE_URL="http://35.168.199.26:3000/"
        const val PINCODE_URL="http://www.postalpincode.in/api/pincode/"
        const val GENERATE_TOKEN="createToken"
        const val CHECK_USER="checkuser"
        const val SIGN_UP="sign-up"
        const val INSERT_USER="insertuser"
        const val UPDATE_USER="modifyUserData"
        const val STATE_POST="getpostofstate"
        const val GROUP_POST="getpostofgroup"
        const val SORT_CONTACT_LIST="sortContactList"
        const val REFER_NUMBER="referNumber"
        const val UP_VOTE="upvote"
        const val DOWN_VOTE="downvote"
        const val ADD_COMMENT="addcomment"
        const val ADD_REPLY="addReply"
        const val GET_COMMENT="getComment"
        const val GET_REPLY="getReply"
        const val CREATE_POST="createPost"
        const val UPDATE_POST="modifyPostData"
        const val FIND_NEAR_GROUP="findneargroup"
        const val FIND_GROUP="findgroup"
        const val LAT_LNG_ADDRESS="getLatLongFromAddress"
        const val USER_PROFILE="getusergroup"
        const val SEND_OTP="sendotp"
        const val MY_NEIGHBOUR="getNeighbours"
        const val OCCUPATION_LIST="occupationList"
        const val HELP="getHelp"
        const val LAUNCH="launchScreenMessage"
        const val MY_POST="userPost"
        var IS_MUTE = false
        //var LAST_POST_ID = -1
        //var LAST_POST_IDD = "-1"

        /*API Constance*/
        const val NAME="name"
        const val EMAIL="email"
        const val MOB_NO="mobno"
        const val PASSWORD="password"
        const val PHONE="phone"
        const val GENDER="gender"
        const val USERTYPE="userType"
        const val DOB="dateOfBirth"
        const val CLIENT_ID="clientid"
        const val CLIENT_SECRET="clientsecret"
        const val CLIENT_ID_DATA="pulkitappdeveloper"
        const val CLIENT_SECRET_DATA="shizasoftwaredeveloper"
        const val USER_ID="userId"
        const val USERID="userid"
        const val STATE="state"
        const val COUNT="count"
        const val POST_ID="postId"
        const val COMMENT_ID="commentId"
        const val CREATED_ON="createdOn"
        const val UPDATED_ON="updatedOn"
        const val COMMENT="text"
        const val BODY_TEXT="bodyText"
        const val IMAGE="image"
        const val VIDIEO="video"
        const val UPDATED_O="updatedO"
        const val UPVOTECOUNT="upvoteCount"
        const val DOWNVOTECOUNT="downvoteCount"
        const val GROUP_ID="groupId"
        const val isState="isState"
        const val CITY="city"
        const val LOCATION="location"
        const val LATITUDE="latitude"
        const val LONGITUDE="longitude"
        const val PAGE="page"
        const val PHONE_NB="phoneNo"

        const val REFER_BY_ID="referedById"
        const val REFER_BY_GROUP_ID="referedByGroupId"
        const val INVITATION_DATE="invitationDate"
        const val REFER_BY_GROUP_NAME="referedByGroupName"

        const val SCREEN_HOME = "home"

        //
        const val FIRST_NAME="firstName"
        const val LAST_NAME="lastName"
        const val OCCUPATION="occupation"
        const val PROFILE_PICTURE="profilePicture"
        const val UPDATE_ON="updatedOn"
        const val PHONE_NO="phoneNo"
        const val LAT_LNG="latLong"
        const val ADDRESS="address"
        const val ZIP="zip"
        const val PINCODE="pinCode"

        //
        val API_POST_HOME = "getPost"



    }
}