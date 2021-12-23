from PIL import Image

def imagecheck(img_path) :
    print (img_path)
    try:
        img=Image.open(img_path)
        # check size of image
        width, height = img.size
        print("This file is an image")
        print(str(width) + "x" + str(height))
        return 1
    except IOError:
        # filename is not an image file
        print("This file is not an image")
        return 0