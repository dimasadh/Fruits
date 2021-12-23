#%%
import os
import cv2
import numpy as np
import json
# from keras.models import load_model

IMG_SIZE = 224

def predict (model, img_path) :
    path_img_test = os.path.join(img_path)
    img_array = cv2.imread(path_img_test) 
    img_test = cv2.resize(img_array, (IMG_SIZE, IMG_SIZE),3)
    np_img_test = np.array(img_test).reshape(-1, 224, 224, 3)
    predict_proba = model.predict(np_img_test)
    predict_result = np.argmax(predict_proba, axis=-1)

    if predict_proba[0][predict_result[0]]*100 < 80 :
        # result = "File yang anda kirim bukan buah (apel, pisang, atau jeruk)"
        result = "Bukan buah"
    elif predict_result[0] == 0 :
        result = "apple"
    elif predict_result[0] == 1 :
        result = "banana"
    elif predict_result[0] == 2 :
        result = "orange"

    data = {}
    data['predict_res'] = result
    data['apple_proba'] = str(predict_proba[0][0]*100)
    data['banana_proba'] = str(predict_proba[0][1]*100)
    data['orange_proba'] = str(predict_proba[0][2]*100)
    print(json.dumps(data))
    return json.dumps(data)

#%%
# img_path = './templates/images\\apple_test_1.jpg'
# model = load_model('./model/vgg16 - 97.h5')
# predict(model, img_path)
# %%
