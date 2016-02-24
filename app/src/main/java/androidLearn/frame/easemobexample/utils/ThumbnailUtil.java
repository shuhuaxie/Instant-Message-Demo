package androidLearn.frame.easemobExample.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Thumbnail generation routines for media provider.
 */

public class ThumbnailUtil {
	private static final String TAG = "ThumbnailUtil";

	/* Maximum pixels size for created bitmap. */
	private static final int MAX_NUM_PIXELS_FULL_THUMBNAIL = 1280 * 768;
	private static final int MAX_NUM_PIXELS_MINI_THUMBNAIL = 512 * 384;
	private static final int MAX_NUM_PIXELS_MICRO_THUMBNAIL = 128 * 128;
	private static final int UNCONSTRAINED = -1;

	/* Options used internally. */
	private static final int OPTIONS_NONE = 0x0;
	private static final int OPTIONS_SCALE_UP = 0x1;

	/**
	 * Constant used to indicate we should recycle the input in {@link #extractThumbnail(Bitmap, int, int, int)} unless the output is the input.
	 */
	private static final int OPTIONS_RECYCLE_INPUT = 0x2;


	private static final int TARGET_SIZE_FULL_THUMBNAIL = UNCONSTRAINED;

	/**
	 * Constant used to indicate the dimension of mini thumbnail.
	 * @hide Only used by media framework and media provider internally.
	 */
	private static final int TARGET_SIZE_MINI_THUMBNAIL = 320;

	/**
	 * Constant used to indicate the dimension of micro thumbnail.
	 * @hide Only used by media framework and media provider internally.
	 */
	private static final int TARGET_SIZE_MICRO_THUMBNAIL = 96;

	/**
	 * This method first examines if the thumbnail embedded in EXIF is bigger than our target
	 * size. If not, then it'll create a thumbnail from original image. Due to efficiency
	 * consideration, we want to let MediaThumbRequest avoid calling this method twice for
	 * both kinds, so it only requests for MICRO_KIND and set saveImage to true.
	 * 
	 * This method always returns a "square thumbnail" for MICRO_KIND thumbnail.
	 * 
	 * @param filePath the path of image file
	 * @param kind
	 * <dd><li>{@link Images.Thumbnails#MINI_KIND}<br>
	 * <dd><li>{@link Images.Thumbnails#MICRO_KIND}<br>
	 * @return Bitmap
	 * 
	 * @hide This method is only used by media framework and media provider internally.
	 */
	public static Bitmap createImageThumbnail(String filePath, int kind) {
		int targetSize;
		int maxPixels;

		switch(kind){
			case Images.Thumbnails.FULL_SCREEN_KIND:
				targetSize = TARGET_SIZE_FULL_THUMBNAIL;
				maxPixels = MAX_NUM_PIXELS_FULL_THUMBNAIL;
				break;
			case Images.Thumbnails.MINI_KIND:
				targetSize = TARGET_SIZE_MINI_THUMBNAIL;
				maxPixels = MAX_NUM_PIXELS_MINI_THUMBNAIL;
				break;
			default:
				targetSize = TARGET_SIZE_MICRO_THUMBNAIL;
				maxPixels = MAX_NUM_PIXELS_MICRO_THUMBNAIL;
				break;
		}

		SizedThumbnailBitmap sizedThumbnailBitmap = new SizedThumbnailBitmap();
		Bitmap bitmap = null;
		int degree = 0;
		String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
		if ("jpg".equals(extension) || "jpeg".equals(extension) || "jpe".equals(extension)) {
//			createThumbnailFromEXIF(filePath, targetSize, maxPixels, sizedThumbnailBitmap);
//			bitmap = sizedThumbnailBitmap.mBitmap;
 			degree = BitmapUtils.getDegress(filePath);
		}

		if (bitmap == null) {
			try {
				FileDescriptor fd = new FileInputStream(filePath).getFD();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFileDescriptor(fd, null, options);
				if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
					return null;
				}
				options.inSampleSize = computeSampleSize(options, targetSize, maxPixels);
				options.inJustDecodeBounds = false;

				options.inDither = false;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);
			} catch (IOException ex) {
				Log.e(TAG, "", ex);
			}
		}

		if (kind == Images.Thumbnails.MICRO_KIND) {
			// now we make it a "square thumbnail" for MICRO_KIND thumbnail
			bitmap = extractThumbnail(bitmap, TARGET_SIZE_MICRO_THUMBNAIL, TARGET_SIZE_MICRO_THUMBNAIL, OPTIONS_RECYCLE_INPUT);
		}

		if(degree != 0){
			Bitmap newBitmap = BitmapUtils.rotateBitmap(bitmap, degree);
			if(newBitmap != null && !newBitmap.equals(bitmap) ){
				bitmap.recycle();
			}
			return newBitmap;
		}

		return bitmap;
	}

	public static Bitmap extractThumbnail(Bitmap source) {
		return extractThumbnail(source, TARGET_SIZE_MICRO_THUMBNAIL, TARGET_SIZE_MICRO_THUMBNAIL, OPTIONS_RECYCLE_INPUT);
	}
	
	/**
	 * Creates a centered bitmap of the desired size.
	 * 
	 * @param source original bitmap source
	 * @param width targeted width
	 * @param height targeted height
	 */
	public static Bitmap extractThumbnail(Bitmap source, int width, int height) {
		return extractThumbnail(source, width, height, OPTIONS_NONE);
	}

	/**
	 * Creates a centered bitmap of the desired size.
	 * 
	 * @param source original bitmap source
	 * @param width targeted width
	 * @param height targeted height
	 * @param options options used during thumbnail extraction
	 */
	public static Bitmap extractThumbnail(Bitmap source, int width, int height, int options) {
		if (source == null) {
			return null;
		}

		float scale;
		if (source.getWidth() < source.getHeight()) {
			scale = width / (float) source.getWidth();
		} else {
			scale = height / (float) source.getHeight();
		}
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		Bitmap thumbnail = transform(matrix, source, width, height, OPTIONS_SCALE_UP | options);
		return thumbnail;
	}

	/*
	 * Compute the sample size as a function of minSideLength
	 * and maxNumOfPixels.
	 * minSideLength is used to specify that minimal width or height of a
	 * bitmap.
	 * maxNumOfPixels is used to specify the maximal size in pixels that is
	 * tolerable in terms of memory usage.
	 *
	 * The function returns a sample size based on the constraints.
	 * Both size and minSideLength can be passed in as IImage.UNCONSTRAINED,
	 * which indicates no care of the corresponding constraint.
	 * The functions prefers returning a sample size that
	 * generates a smaller bitmap, unless minSideLength = IImage.UNCONSTRAINED.
	 *
	 * Also, the function rounds up the sample size to a power of 2 or multiple
	 * of 8 because BitmapFactory only honors sample size this way.
	 * For example, BitmapFactory downsamples an image by 2 even though the
	 * request is 3. So we round up the sample size to avoid OOM.
	 */
	private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;

			switch (initialSize){
				case 1:
					return 1;
				case 2:
					return 2;
				case 3:
				case 4:
				case 5:
				case 6:
					return 4;
				case 7:
				case 8:
					return 8;
				default:
					while (roundedSize < initialSize) {
						roundedSize <<= 1;
					}
					break;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == UNCONSTRAINED) && (minSideLength == UNCONSTRAINED)) {
			return 1;
		} else if (minSideLength == UNCONSTRAINED) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * Make a bitmap from a given Uri, minimal side length, and maximum number of pixels.
	 * The image data will be read from specified pfd if it's not null, otherwise
	 * a new input stream will be created using specified ContentResolver.
	 * 
	 * Clients are allowed to pass their own BitmapFactory.Options used for bitmap decoding. A
	 * new BitmapFactory.Options will be created if options is null.
	 */
	private static Bitmap makeBitmap(int minSideLength, int maxNumOfPixels, Uri uri, ContentResolver cr, ParcelFileDescriptor pfd, BitmapFactory.Options options) {
		Bitmap b = null;
		try {
			if (pfd == null) {
				pfd = makeInputStream(uri, cr);
			}
			if (pfd == null) {
				return null;
			}
			if (options == null) {
				options = new BitmapFactory.Options();
			}

			FileDescriptor fd = pfd.getFileDescriptor();
			options.inSampleSize = 1;
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFileDescriptor(fd, null, options);
			if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
				return null;
			}
			options.inSampleSize = computeSampleSize(options, minSideLength, maxNumOfPixels);
			options.inJustDecodeBounds = false;

			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			b = BitmapFactory.decodeFileDescriptor(fd, null, options);
		} catch (OutOfMemoryError ex) {
			Log.e(TAG, "Got oom exception ", ex);
			return null;
		} finally {
			closeSilently(pfd);
		}
		return b;
	}

	private static void closeSilently(ParcelFileDescriptor c) {
		if (c == null) {
			return;
		}
		try {
			c.close();
		} catch (Throwable t) {
			// do nothing
		}
	}

	private static ParcelFileDescriptor makeInputStream(Uri uri, ContentResolver cr) {
		try {
			return cr.openFileDescriptor(uri, "r");
		} catch (IOException ex) {
			return null;
		}
	}

	/**
	 * Transform source Bitmap to targeted width and height.
	 */
	private static Bitmap transform(Matrix scaler, Bitmap source, int targetWidth, int targetHeight, int options) {
		boolean scaleUp = (options & OPTIONS_SCALE_UP) != 0;
		boolean recycle = (options & OPTIONS_RECYCLE_INPUT) != 0;

		int deltaX = source.getWidth() - targetWidth;
		int deltaY = source.getHeight() - targetHeight;
		if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
			/*
			 * In this case the bitmap is smaller, at least in one dimension,
			 * than the target.  Transform it by placing as much of the image
			 * as possible into the target and leaving the top/bottom or
			 * left/right (or both) black.
			 */
			Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b2);

			int deltaXHalf = Math.max(0, deltaX / 2);
			int deltaYHalf = Math.max(0, deltaY / 2);
			Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf + Math.min(targetWidth, source.getWidth()), deltaYHalf + Math.min(targetHeight, source.getHeight()));
			int dstX = (targetWidth - src.width()) / 2;
			int dstY = (targetHeight - src.height()) / 2;
			Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight - dstY);
			c.drawBitmap(source, src, dst, null);
			if (recycle) {
				source.recycle();
			}
			return b2;
		}
		float bitmapWidthF = source.getWidth();
		float bitmapHeightF = source.getHeight();

		float bitmapAspect = bitmapWidthF / bitmapHeightF;
		float viewAspect = (float) targetWidth / targetHeight;

		if (bitmapAspect > viewAspect) {
			float scale = targetHeight / bitmapHeightF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			} else {
				scaler = null;
			}
		} else {
			float scale = targetWidth / bitmapWidthF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			} else {
				scaler = null;
			}
		}

		Bitmap b1;
		if (scaler != null) {
			// this is used for minithumb and crop, so we want to filter here.
			b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), scaler, true);
		} else {
			b1 = source;
		}

		if (recycle && b1 != source) {
			source.recycle();
		}

		int dx1 = Math.max(0, b1.getWidth() - targetWidth);
		int dy1 = Math.max(0, b1.getHeight() - targetHeight);

		Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth, targetHeight);

		if (b2 != b1) {
			if (recycle || b1 != source) {
				b1.recycle();
			}
		}

		return b2;
	}

	/**
	 * SizedThumbnailBitmap contains the bitmap, which is downsampled either from
	 * the thumbnail in exif or the full image.
	 * mThumbnailData, mThumbnailWidth and mThumbnailHeight are set together only if mThumbnail
	 * is not null.
	 * 
	 * The width/height of the sized bitmap may be different from mThumbnailWidth/mThumbnailHeight.
	 */
	private static class SizedThumbnailBitmap {
		public byte[] mThumbnailData;
		public Bitmap mBitmap;
		public int mThumbnailWidth;
		public int mThumbnailHeight;
	}

	/**
	 * Creates a bitmap by either downsampling from the thumbnail in EXIF or the full image.
	 * The functions returns a SizedThumbnailBitmap,
	 * which contains a downsampled bitmap and the thumbnail data in EXIF if exists.
	 */
	private static void createThumbnailFromEXIF(String filePath, int targetSize, int maxPixels, SizedThumbnailBitmap sizedThumbBitmap) {
		if (filePath == null) {
			return;
		}

		ExifInterface exif = null;
		byte[] thumbData = null;
		try {
			exif = new ExifInterface(filePath);
			if (exif != null) {
				thumbData = exif.getThumbnail();
			}
		} catch (IOException ex) {
			Log.w(TAG, ex);
		}

		BitmapFactory.Options fullOptions = new BitmapFactory.Options();
		BitmapFactory.Options exifOptions = new BitmapFactory.Options();
		int exifThumbWidth = 0;
		int fullThumbWidth = 0;

		// Compute exifThumbWidth.
		if (thumbData != null) {
			exifOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(thumbData, 0, thumbData.length, exifOptions);
			exifOptions.inSampleSize = computeSampleSize(exifOptions, targetSize, maxPixels);
			exifThumbWidth = exifOptions.outWidth / exifOptions.inSampleSize;
		}

		// Compute fullThumbWidth.
		fullOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, fullOptions);
		fullOptions.inSampleSize = computeSampleSize(fullOptions, targetSize, maxPixels);
		fullThumbWidth = fullOptions.outWidth / fullOptions.inSampleSize;

		// Choose the larger thumbnail as the returning sizedThumbBitmap.
		if (thumbData != null && exifThumbWidth >= fullThumbWidth) {
			int width = exifOptions.outWidth;
			int height = exifOptions.outHeight;
			exifOptions.inJustDecodeBounds = false;
			sizedThumbBitmap.mBitmap = BitmapFactory.decodeByteArray(thumbData, 0, thumbData.length, exifOptions);
			if (sizedThumbBitmap.mBitmap != null) {
				sizedThumbBitmap.mThumbnailData = thumbData;
				sizedThumbBitmap.mThumbnailWidth = width;
				sizedThumbBitmap.mThumbnailHeight = height;
			}
		} else {
			fullOptions.inJustDecodeBounds = false;
			sizedThumbBitmap.mBitmap = BitmapFactory.decodeFile(filePath, fullOptions);
		}
	}
}

